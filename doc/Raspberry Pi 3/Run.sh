#!/bin/bash
cd ~/AirController || exit
mvn exec:java -Dexec.mainClass="org.airController.Main"

# GPIO test
# mvn exec:java -Dexec.mainClass="org.airController.gpio.GpioPinImpl"

# DHT22 test
# mvn exec:java -Dexec.mainClass="org.airController.sensor.IndoorAirMeasurement"