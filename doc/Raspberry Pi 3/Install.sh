#!/bin/bash
cd
sudo apt install maven
sudo apt install openjdk-17-jdk
sudo apt remove openjdk-11-jre-headless
git clone https://github.com/maeseee/AirController.git
sudo apt-get --yes install git-core gcc make
git clone https://github.com/WiringPi/WiringPi --branch master --single-branch wiringpi
cd wiringpi
sudo ./build