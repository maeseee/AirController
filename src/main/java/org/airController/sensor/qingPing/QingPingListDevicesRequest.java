package org.airController.sensor.qingPing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

class QingPingListDevicesRequest {
    private static final Logger logger = LogManager.getLogger(QingPingListDevicesRequest.class);

    private final URI uri;

    public QingPingListDevicesRequest(URI uri) {
        this.uri = uri;
    }

    public Optional<String> sendRequest(String accessToken) {
        String responseFromUrl = null;
        try {
            final HttpURLConnection connection = getConnection(accessToken);

            final int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                responseFromUrl = readResponseFromServer(connection.getInputStream());
            }
        } catch (Exception exception) {
            logger.error("QingPingListDevicesRequest failure: ", exception);
            return Optional.empty();
        }
        return Optional.ofNullable(responseFromUrl);
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
