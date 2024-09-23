package org.airController.sensor.qingPing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

class QingPingListDevicesRequest {
    private final URI uri;

    public QingPingListDevicesRequest(URI uri) {
        this.uri = uri;
    }

    public String sendRequest(String accessToken) throws IOException, URISyntaxException, CommunicationException {
        final HttpURLConnection connection = getConnection(accessToken);
        final int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new CommunicationException(
                    "QingPingListDevicesRequest failure with responseCode " + responseCode + " and message " + connection.getResponseMessage());
        }
        return readResponseFromServer(connection.getInputStream());
    }

    private HttpURLConnection getConnection(String accessToken) throws IOException, URISyntaxException {
        final URI uriWithParameter = new URI(uri.toString() + "?timestamp=" + LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        final HttpURLConnection connection = (HttpURLConnection) uriWithParameter.toURL().openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        return connection;
    }

    private String readResponseFromServer(InputStream inputStream) throws IOException {
        final BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        final StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}
