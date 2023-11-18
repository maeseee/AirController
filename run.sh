#!/bin/sh
cd /home/pi/AirController || exit
while true; do
mvn exec:java -Dexec.mainClass="org.airController.Main"
sleep 5s # just sleep a little before restart again
done

# Run application on raspberry pi
# mvn exec:java -Dexec.mainClass="org.airController.Main"

# GPIO test
# mvn exec:java -Dexec.mainClass="org.airController.gpio.GpioPinImpl"

# DHT22 test
# mvn exec:java -Dexec.mainClass="org.airController.sensor.IndoorAirMeasurement"

# Read Environment test
# mvn exec:java -Dexec.mainClass="org.airController.util.EnvironmentVariable"

# Restart service
# sudo systemctl restart airController.service