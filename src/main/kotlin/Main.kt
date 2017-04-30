import kotlin.system.exitProcess

/**
 * Runs the detector.
 * If a portal is present, fingerprinting/resolution will begin.
 * Otherwise, the program exits.
 */

fun main(args: Array<String>) {
    val detector = PortalDetector()
    val result = detector.checkPortalPresence()

    if (!result.present && result.errorStatus == ErrorStatus.SUCCEEDED) {
        exitProcess(0)
    }

    if (result.errorStatus != ErrorStatus.SUCCEEDED) {
        println("An error occurred: ${result.errorStatus}")
        exitProcess(127)
    }


}