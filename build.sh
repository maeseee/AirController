#!/bin/bash
cd /home/pi/AirController || exit
sudo systemctl stop airController.service
mvn clean install
sudo systemctl start airController.service