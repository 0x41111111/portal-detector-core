package detector.registry

/**
 * An action to take that results in the resolution of a given captive portal, thus granting Internet access.
 * At present, the only available action is to hit a URL.
 */
class PortalResolution() {
    // The URL that should be hit.
    // TODO: add and document string substitution markers (portal base URL, etc)
    var url: String = "example.com"

    // The HTTP request method to use.
    var method: String = "GET"

    // A list of parameters to send to the given URL.
    // TODO: see above about string substitution markers
    var parameters: Map<String, String>? = null

    // A list of headers to send to the given URL.
    // something something string substitution
    var headers: Map<String, String>? = null

}