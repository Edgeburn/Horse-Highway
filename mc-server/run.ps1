# Start debug Minecraft server
Copy-Item -Path "..\target\HorseHighway-beta-5.jar" -Destination "plugins\horsehighway.jar" -Verbose
java -Xmx8G -Xms8G -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar server.jar nogui
