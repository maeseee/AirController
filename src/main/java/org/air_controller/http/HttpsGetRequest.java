package org.air_controller.http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Optional;

public class HttpsGetRequest {
    private static final Logger logger = LogManager.getLogger(HttpsGetRequest.class);

    public Optional<String> sendRequest(String url) {
        String responseFromUrl = null;
        try {
            final URI uri = new URI(url);
            final HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("GET");

            final int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                responseFromUrl = readResponseFromServer(connection.getInputStream());
            }
        } catch (Exception exception) {
            logger.error("HttpsGetRequest failure: ", exception);
            return Optional.empty();
        }
        return Optional.ofNullable(responseFromUrl);
    }

    private String readResponseFromServer(InputStream inputStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}
