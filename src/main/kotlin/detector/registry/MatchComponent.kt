package detector.registry

/**
 * An HTTP request component.
 */
enum class MatchComponent {
    // The URL of the portal page.
    URL,
    // A header in the portal's HTTP response.
    HEADER,
    // An HTML selector that will be used against the portal's HTML response.
    HTML_SELECTOR,
    // A default value.
    UNSET
}