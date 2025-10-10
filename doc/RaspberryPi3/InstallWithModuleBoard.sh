#!/bin/bash
cd || exit

# Install some packages
sudo apt install maven
sudo apt install openjdk-17-jdk
sudo apt remove openjdk-11-jre-headless
sudo apt install liblog4j2-java

# Checkout AirController
git clone https://github.com/maeseee/AirController.git

# Checkout and install wiringpi
sudo apt-get --yes install git-core gcc make
git clone https://github.com/WiringPi/WiringPi --branch master --single-branch wiringpi
cd wiringpi || exit
sudo ./build

# Set startup pin config (RaspberryPi will not run without failure without this config when connected to the PiOT Relay Board)
sudo tee -a /boot/config.txt << EOF
dtoverlay=w1-gpio
gpio=5=op,dh
gpio=6=op,dl
gpio=13=op,dl
gpio=19=op,dl
EOF

# Config startup behaviour
cd || exit
echo "export weather_api_key=<api_key>" >> /home/pi/.bashrc
echo "export qingping_app_secret=<secret>" >> /home/pi/.bashrc
echo "export mariaDdPassword=<secret>" >> /home/pi/.bashrc
cd AirController || exit
# Add variables to airController.service as well
sudo cp doc/RaspberryPi3/airController.service /etc/systemd/system/
sudo systemctl enable airController.service # Enable the service to start at boot time
chmod +x run.sh

# See the log of the systemd service
journalctl -fu airController.service -n 10

# Set variables on windows machine
#setx weather_api_key "<api_key>"
#setx qingping_app_secret "<app_secret>"

# See GPIO config
gpio readall

# Write 1 on gpio 23
gpio write 23 1

# Check dependencies
mvn dependency:analyze
