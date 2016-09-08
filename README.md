## Synopsis
This program was written as a proof of concept, it consists of a server application and a client application. The client application requests the current time and date from the server application, and ther server application responds to it (with a message of the day, unique to each day of the week). The server sends the response in multiple packets rather than passing the message through the transport service. It was written to acquire and apply knowledge about networking protocols. For course CPS-706 (Networks)

## Notes

It was written as a proof of concept, and it is discouraged from utilizing in the real-world without further modifications.

## Running & Compiling Instructions
In order to run the application, please run the following commands in sequence:

---------------------------------------------------------------------------------------------------------------------------------

(skip if already compiled)
- Ensure you're in correct directory
- "javac A2server.java"
- "javac guiClient.java"

then first run the server
- "java A2server <portNumber>" where <portNumber> is a valid port number 1025-49151
i.e "java A2server 4444"

then run client
- "java guiClient <portNumber> <hostName>" where <portNumber> is the same port number
	the server is using and <hostName> is the hostname or IP of the server 
	(if using on same machine then localhost)

i.e "java guiClient 4444 localhost"

---------------------------------------------------------------------------------------------------------------------------------

Alternatively, can run the following code in sequence:

- "javac A2server.java && java A2server <portNumber>" where <portNumber> is a valid port number 1025-65535
- "javac guiClient.java && java guiClient <portNumber> <hostName>" where <portNumber> is the same port number
								   the server is using and <hostName> is the hostname or 
								   IP of the server (if using on same machine then localhost)

Then the application will run correctly.
