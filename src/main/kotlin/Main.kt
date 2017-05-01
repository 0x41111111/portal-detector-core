import detector.ErrorStatus
import detector.PortalDetector
import detector.registry.PortalRegistry
import kotlin.system.exitProcess

/**
 * Runs the detector.
 * If a portal is present, registry/resolution will begin.
 * Otherwise, the program exits.
 */

fun main(args: Array<String>) {
    println("Detecting captive portal...")

    val detector = PortalDetector()
    val result = detector.checkPortalPresence()

    /*if (!result.present && result.errorStatus == ErrorStatus.SUCCEEDED) {
        println("No captive portal was detected.")
        exitProcess(0)
    }*/

    if (result.errorStatus != ErrorStatus.SUCCEEDED) {
        println("An error occurred: ${result.errorStatus}")
        exitProcess(127)
    }

    val registry = PortalRegistry()
    registry.populate()

    registry.getPortal(result)
}