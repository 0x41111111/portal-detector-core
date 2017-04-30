import okhttp3.Response

/**
 * A response object that contains information about whether or not a captive portal is present.
 */
class PortalDetectionStatus {
    // Whether or not a captive portal is present.
    var present = true

    // Whether or not a network error occurred while attempting to detect the portal.
    var errorStatus = ErrorStatus.SUCCEEDED

    // The response returned from the remote server. Null if `present` is `false`.
    var response: Response? = null
}

