# Stage 1: Bauen der App
FROM node:22-alpine AS build-stage
WORKDIR /app

COPY package*.json ./
RUN npm install
COPY . .
RUN npx ng build --configuration=production
RUN npx ng build --configuration=production --output-path=dist/aircontroller-app

# Stage 2: Ausliefern mit Nginx
FROM nginx:stable-alpine
RUN rm -rf /usr/share/nginx/html/* # Delete standard page from Nginx
COPY --from=build-stage /app/dist/aircontroller-app/browser /usr/share/nginx/html
# Eigene Nginx-Konfiguration für Angular Routing hinzufügen
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]

# TODO Reverse Proxy auf dem Synology
# unter Systemsteuerung > Anmeldeportal
# Quelle: http://aircontroller.synology.me (Port 80/443)
# Ziel: http://localhost:8085
