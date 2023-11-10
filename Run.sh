#!/bin/bash
cd ~/AirController || exit
mkdir -p "log"
now=$(date +"%Y_%m_%d")
log_filename=log_${now}.log
echo "AirController started on ${now}" | tee "${log_filename}"

while true; do
mvn exec:java -Dexec.mainClass="org.airController.Main" | tee -a "${log_filename}"
echo "AirController has been restarted on $(date)" | tee -a "${log_filename}"
sleep 5s # just sleep a little before restart again
done

# Run application on raspberry pi
# mvn exec:java -Dexec.mainClass="org.airController.Main"

# GPIO test
# mvn exec:java -Dexec.mainClass="org.airController.gpio.GpioPinImpl"

# DHT22 test
# mvn exec:java -Dexec.mainClass="org.airController.sensor.IndoorAirMeasurement"

# Add the following command to the /etc/rc.local file
# /home/pi/AirController/Run.sh &