package detector.registry

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import detector.PortalDetectionStatus
import java.io.File

/**
 * Contains all the captive portal definitions loaded off disc.
 */
class PortalRegistry {
    // The directory that contains all the portal definitions to load.
    val directory = "./registry"

    // Every available portal definition.
    val portals: MutableList<CaptivePortalDefinition> = mutableListOf()

    // When called, this function recurses the registry directory looking for portal definitions and registers them.
    fun populate() {
        println("Loading portal definitions.")

        val mapper = ObjectMapper(YAMLFactory())

        File(directory).walkTopDown().forEach {
            if (it.isDirectory) {
                return@forEach // we obviously can't use directories as portal definitions
            }

            if (it.extension != "yml") {
                return@forEach // this isn't a portal definition
            }

            println("Loading ${it.nameWithoutExtension}")

            val fileContent = it.readText()
            val definition = mapper.readValue(fileContent, CaptivePortalDefinition::class.java)

            portals.add(definition)

            println("Loaded ${it.nameWithoutExtension}: ${definition.displayName}")
        }
    }

    fun getPortal(status: PortalDetectionStatus): CaptivePortalDefinition? {
        portals.forEach {
            val match = it.match(status.response!!, status.html!!)
            if (match) {
                println("Found a match: ${it.displayName}")
                return it
            }
        }

        println("No captive portal definitions matched the detected captive portal.")
        return null
    }
}