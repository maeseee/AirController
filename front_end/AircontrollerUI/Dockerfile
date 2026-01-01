# Stage 1: Bauen der App
FROM node:18-alpine AS build
WORKDIR /app
# Nur Package-Dateien kopieren für effizientes Caching
COPY package*.json ./
RUN npm install
# Restlichen Code kopieren und bauen
COPY .. .
RUN npm run build --configuration=production

# Stage 2: Ausliefern mit Nginx
FROM nginx:stable-alpine
COPY --from=build /app/dist/AircontrollerUI /usr/share/nginx/html
# Eigene Nginx-Konfiguration für Angular Routing hinzufügen
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
