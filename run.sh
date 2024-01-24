#!/bin/bash
cd /home/pi/AirController || exit
mvn exec:java -Dexec.mainClass="org.airController.Main"

# Run application on raspberry pi
# mvn exec:java@run-main

# GPIO test
# mvn exec:java@run-gpio-test

# DHT22 test
# mvn exec:java@run-indoor-sensor-test

# Restart service
# sudo systemctl restart airController.service

# stop service
# sudo systemctl stop airController.service