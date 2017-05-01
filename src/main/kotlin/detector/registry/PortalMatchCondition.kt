package detector.registry

/**
 * A condition that must be matched in order for a captive portal definition to be selected.
 * All conditions in a portal definition must match in order to cause a definition match.
 * An exception to this occurs when the `causesCompleteMatch` field is set to `true`, which short-circuits the condition check to immediately return a match.
 */
data class PortalMatchCondition(
        // The component of the HTTP response to match against.
        var component: MatchComponent = MatchComponent.UNSET,

        // The key to match against. This can be an HTML selector, header name or string fragment.
        // For URLs, this will be used as an argument to .contains()
        // If a header or an element referred to by an HTML selector is missing, this condition will immediately evaluate to false.
        var key: String = "unset",

        // The value expected in the response. If this matches what's in the response, this condition will evaluate to true.
        // For URLs, this value is ignored.
        // For HTML selectors, this value will be compared against the textual content of the first selected element.
        // For headers, this value will be compared against the value of the header referred to by the `key` field.
        // All comparisons are case insensitive.
        var value: String = "unset",

        // Should this condition result in an immediate portal definition match?
        // If set, no further conditions in the portal definition will be processed.
        var causesCompleteMatch: Boolean = true
)