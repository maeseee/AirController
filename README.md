# AirController

The goal of this application is to control the humidity in the air with a controlled apartment ventilation while minimizing the heating cost. 

The controlling mechanisms are on one hand to switch the air flow on or off. On the other hand a humidity exchanger can be turned on or off. Anyway, to get access to these control mechanisms, the system had to be rewired and a raspberry pi was placed to run the software on. 

Following some requirements:
- The system has to be turned on for at least 10 minutes each hour.
- The system must run for at least 4 hours straight.
- To reduce the heating cost, in winter time the system must run when the outdoor temperatures are the highest.
- To reduce the indoor temperature, In summer the system must run when the outdoor temperatures are the lowest.
- For the outdoor temperature and humidity an internet service must be used. 
- For the indoor temperature and humidity sensor connected to the raspberry pi with I2C can be used.
- Indoor- and outdoor sensor values must be logged at least each 30 minutes.
- Humidity should be controlled to get as close as possible to 50%. As it is a passive system, this will not always be possible. 
