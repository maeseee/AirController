# Docker

## Update Docker on Synology NAS

1. Compile and create jar file with `mvn clean install -DskipTests`
2. Open Browser and login (https://192.168.50.12:5001)
3. Open `File Station`
4. Go to docker/air_controller
5. `Upload - Override`
6. Choose `AirController-1.0-SNAPSHOT-jar-with-dependencies.jar`
7. Open `Package Center`
8. Go to `Installed`/`Container Manager`
9. Go to `Container` and choose `air-controller`
10. Press `Action`/`Restart`