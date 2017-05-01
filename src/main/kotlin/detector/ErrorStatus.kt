package detector

/**
 *
 */
enum class ErrorStatus {
    SUCCEEDED,

    // A non 2xx-3xx HTTP response code was received.
    HTTP_RESPONSE_CODE_ERROR,

    // The response has no body.
    MISSING_BODY,

    // A network-related exception occurred.
    NETWORK_EXCEPTION,

    // An unknown error occurred.
    UNKNOWN_ERROR
}