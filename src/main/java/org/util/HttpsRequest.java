package org.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Optional;

public class HttpsRequest {

    private final URI uri;

    public HttpsRequest(URI uri) {
        this.uri = uri;
    }

    public Optional<String> sendRequest() {
        String responseFromUrl = "";
        try {
            final HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("GET");

            final int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                responseFromUrl = readResponseFromServer(connection);
            }
        } catch (Exception e) {
            return Optional.empty();
        }
        return Optional.of(responseFromUrl);
    }

    private String readResponseFromServer(HttpURLConnection connection) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}
