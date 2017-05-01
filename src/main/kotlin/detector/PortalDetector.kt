package detector

import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.IOException

/**
 * Responsible for probing the network and determining whether or not there's a captive portal present.
 */

class PortalDetector {
    // The User-Agent header to send when testing for a captive portal.
    val userAgent = "CaptiveNetworkSupport"

    // The URL that should be used to determine whether or not there is a captive portal present.
    val canaryURL = "http://captive.apple.com/library/test/success.html"

    // The HTML selector that contains the value in the variable below
    val selector = "title"

    // If the HTML element referred to by the above selector contains this value, the portal test is deemed to have passed.
    val expectedResult = "Success"

    // Returns true if a portal is present.
    fun checkPortalPresence(): PortalDetectionStatus {
        val result = PortalDetectionStatus()
        val call = getCanaryContents()

        try {
            val it = call.execute()
            if (!it.isSuccessful) {
                // TODO: tell the user that something replied with a failure code
                // this is probably the legitimate server or captive portal experiencing issues
                result.errorStatus = ErrorStatus.HTTP_RESPONSE_CODE_ERROR
                return result
            }

            if (it.body() == null) {
                // the server sent an empty response
                // TODO: determine an appropriate failure message
                result.errorStatus = ErrorStatus.MISSING_BODY
                return result
            }

            val responseBody = it.body()!!
            // otherwise, parse the response body as an HTML document
            val doc = Jsoup.parse(responseBody.string())

            // look for a given HTML element in the response body
            // if it matches the expected value, there's no portal in place (or it's faking the portal detection page)
            val element = doc.select(selector).first()
            val contents = element.text()

            result.response = it
            result.html = doc

            if (contents != expectedResult) {
                // A captive portal is present. Proceed to the identification/resolution stage.
                result.present = true

                return result
            }

            // if we're here, all the other checks passed
            // there's no captive portal in the way
            result.present = false
            result.errorStatus = ErrorStatus.SUCCEEDED

        } catch (e: IOException) {
            val r = PortalDetectionStatus()
            r.errorStatus = ErrorStatus.NETWORK_EXCEPTION
            return r
        } catch (e: Exception) {
            val r = PortalDetectionStatus()
            r.errorStatus = ErrorStatus.UNKNOWN_ERROR
            return r
        }

        return result
    }

    // Performs a request against a URL to be used when checking for a captive portal.
    fun getCanaryContents(): Call {
        val client = OkHttpClient.Builder()
                .followRedirects(true) // most, if not all, captive portals rely on HTTP redirects
                .build()

        val request = Request.Builder()
                .url(canaryURL)
                .header("User-Agent", userAgent)
                .build()

        return client.newCall(request)
    }
}