#!/bin/bash
cd || exit
sudo apt update
sudo apt upgrade

# Set ip

# Install some packages
sudo apt install maven
sudo apt install openjdk-17-jdk
sudo apt install liblog4j2-java

# Checkout AirController
git clone https://github.com/maeseee/AirController.git

# Config startup behaviour
cd || exit
echo "export weather_api_key=<api_key>" >> /home/pi/.bashrc
echo "export qingping_app_secret=<secret>" >> /home/pi/.bashrc
echo "export dbPassword=<secret>" >> /home/pi/.bashrc
cd AirController || exit
# Add variables to airController.service as well
sudo cp doc/RaspberryPi3/airController.service /etc/systemd/system/
sudo systemctl enable airController.service # Enable the service to start at boot time
chmod +x run.shj

# See the log of the systemd service
journalctl -fu airController.service -n 10

# Set variables on windows machine
#setx weather_api_key "<api_key>"
#setx qingping_app_secret "<app_secret>"

# See GPIO config
gpio readall

# Check dependencies
mvn dependency:analyze
