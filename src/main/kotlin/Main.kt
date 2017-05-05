import detector.ErrorStatus
import detector.PortalDetector
import detector.registry.PortalRegistry
import detector.registry.PortalResolutionStatus
import kotlin.system.exitProcess

/**
 * Runs the detector.
 * If a captive portal is present, it will be fingerprinted and resolved.
 * Otherwise, the program exits cleanly.
 */

fun main(args: Array<String>) {
    println("Detecting captive portal...")

    val detector = PortalDetector()
    val result = detector.checkPortalPresence()

    if (!result.present && result.errorStatus == ErrorStatus.SUCCEEDED) {
        println("No captive portal was detected.")
        exitProcess(0)
    }

    if (result.errorStatus != ErrorStatus.SUCCEEDED) {
        println("An error occurred: ${result.errorStatus}")
        exitProcess(127)
    }

    println("A captive portal is present.")

    val registry = PortalRegistry()
    registry.populate()

    val portal = registry.getPortal(result)
    if (portal == null) {
        println("No matching captive portal definitions found, exiting")
        exitProcess(127)
    }

    println("Attempting to resolve the detected captive portal")
    val resolutionStatus = portal.resolve(result.response!!)
    if (resolutionStatus == PortalResolutionStatus.UNRESOLVEABLE) {
        println("The captive portal on this network has been flagged as unresolveable.")
        println("This can occur due to the portal requiring payment or login credentials.")
        println("You will need to manually log into this network.")
        exitProcess(1)
    }

    println("Verifying Internet connectivity...")
    val secondResult = detector.checkPortalPresence()

    if (!secondResult.present && secondResult.errorStatus == ErrorStatus.SUCCEEDED) {
        println("You should now have Internet connectivity.")
        println("Start your browser/VPN client/... now.")

        exitProcess(0)
    }

    println("The captive portal couldn't be resolved due to an error occurring. You will need to manually log into this network.")
    exitProcess(127)
}