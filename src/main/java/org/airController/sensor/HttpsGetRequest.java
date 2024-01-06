package org.airController.sensor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Optional;

class HttpsGetRequest {

    private final URI uri;

    public HttpsGetRequest(URI uri) {
        this.uri = uri;
    }

    public Optional<String> sendRequest() {
        String responseFromUrl = null;
        try {
            final HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("GET");

            final int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                responseFromUrl = readResponseFromServer(connection.getInputStream());
            }
        } catch (Exception e) {
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
