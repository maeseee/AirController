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

## Create Docker container on Synology NAS

1. Update the container name in the [compose](compose.yaml) file
2. Create the docker folder (e.g. \\DS224P\docker\air_controller_java25)
3. On Synology Container Manager, create a new Project
4. Give it a unique name (e.g. air-controller-with-java-25)
5. No web-portal is needed
6. Build and run the new docker in parallel 