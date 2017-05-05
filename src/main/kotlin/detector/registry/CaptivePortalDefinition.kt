package detector.registry

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jsoup.nodes.Document
import java.net.URI

/**
 * Contains all the information needed to make up a captive portal definition.
 */
class CaptivePortalDefinition() {
    // A short name for this particular captive portal.
    // Example: "acme_guest_network"
    var name: String = "unknown"

    // A human-friendly name for this captive portal.
    // Example: "ACME Guest Network"
    var displayName: String = "Unknown"

    // The criteria needed to match a portal detection response against this definition.
    var criteria: List<PortalMatchCondition> = listOf()

    // If this is not null, this means that the portal can be automatically resolved.
    // This must be set to `null` for paid captive portals.
    // This must also be set to `null` for portals that have reCAPTCHA active or other anti-bot measures.
    var resolution: PortalResolution? = null

    // Given an OkHttp Response and a JSoup Document, this function attempts to match the criteria in this class against the response's properties.
    fun match(r: Response, doc: Document): Boolean {
        criteria.forEach {
            when (it.component) {
                MatchComponent.HEADER -> {
                    val headerName = it.key
                    val headerContents = it.value

                    val header = r.header(headerName)
                    if (header == null || header.toLowerCase() != headerContents.toLowerCase()) {
                        return false
                    }

                    // the header in the response matched
                }

                MatchComponent.HTML_SELECTOR -> {
                    val selector = it.key
                    val expectedContent = it.value

                    val element = doc.select(selector).first()
                    val content = element.text()

                    if (content != expectedContent) {
                        return false
                    }

                    // the selector and expected content matched
                }

                MatchComponent.URL -> {
                    val responseUrl = r.request().url().toString()
                    if (!responseUrl.contains(it.key)) {
                        return false
                    }
                }

                else -> {
                    println("Warning: a component type hasn't been set for $displayName (${it.key}), failing match()")
                    return false
                }
            }

            if (it.causesCompleteMatch) {
                return true
            }
        }

        // All the criteria matched, thus this portal definition matches the response obtained from the detector.
        return true
    }

    // If the portal definition contains a resolution section, this method should be called to resolve the portal.
    // If the portal can't be resolved, this method will immediately return a status code indicating that the portal is unresolveable.
    fun resolve(r: Response): PortalResolutionStatus {
        if (resolution == null) {
            return PortalResolutionStatus.UNRESOLVEABLE
        }

        val client = OkHttpClient()
        var builder = Request.Builder()
        var uri = URI(resolution!!.url)

        if (resolution!!.headers != null) {
            resolution!!.headers?.forEach { k, v -> builder = builder.addHeader(k, v) }
        }

        if (resolution!!.parameters != null) {
            resolution!!.parameters?.forEach { k, v ->
                uri = appendToURI(uri, "$k=$v")
            }
        }

        builder = builder.url(uri.toURL())

        val request = builder.build()
        val call = client.newCall(request)
        val response: Response

        try {
            response = call.execute()
        } catch (e: Exception) {
            return PortalResolutionStatus.RESOLUTION_ERROR
        }

        if (!response.isSuccessful) {
            return PortalResolutionStatus.RESOLUTION_ERROR
        }

        return PortalResolutionStatus.RESOLVED
    }

    private fun appendToURI(old: URI, segment: String): URI {
        var queryString = old.query

        if (queryString == null) {
            queryString = segment
        } else {
            queryString = "$queryString&$segment"
        }

        val new = URI(old.scheme, old.authority, old.path, queryString, old.fragment)
        return new
    }
}

