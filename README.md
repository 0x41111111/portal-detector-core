# About

This is an (incomplete) tool that allows the end user to automatically dismiss captive portals commonly found on open wireless networks.

It works by:

* Making a request to a server containing an already-known response (captive.apple.com at present)
* Checking the response body to see whether or not the response contains a known good value
* If the previous step fails, the response is tested against a list of captive portal definitions until a match is found or the list is exhausted.
* Performing a resolution as described in the portal definition file.

Portal definition files are simply YAML files containing a user-friendly + short name for the captive portal being described, as well as a series of conditions required for this definition to match. A resolution can also be provided if the portal doesn't need payment or authentication credentials.

The basic functionality's there, but it's not yet complete.
