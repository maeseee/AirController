#!/bin/bash
cd || exit
sudo apt install maven
sudo apt install openjdk-17-jdk
sudo apt remove openjdk-11-jre-headless
git clone https://github.com/maeseee/AirController.git
sudo apt-get --yes install git-core gcc make
git clone https://github.com/WiringPi/WiringPi --branch master --single-branch wiringpi
cd wiringpi || exit
sudo ./build

sudo tee -a /boot/config.txt << EOF
dtoverlay=w1-gpio
gpio=5=op,dh
gpio=6=op,dl
gpio=13=op,dl
gpio=19=op,dl
EOF

#See GPIO config
gpio readall