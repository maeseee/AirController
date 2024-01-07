package org.airController.sensor;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

class QingPingAccessTokenRequest {

    private final URI uri;
    private final String credentials;

    public QingPingAccessTokenRequest(URI uri, String credentials) {
        this.uri = uri;
        this.credentials = credentials;
    }

    public Optional<String> sendRequest() {
        String responseFromUrl = null;
        try {
            final HttpURLConnection connection = getConnection(credentials);

            final int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                responseFromUrl = readResponseFromServer(connection.getInputStream());
            }
            connection.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(responseFromUrl);
    }

    private HttpURLConnection getConnection(String base64Credentials) throws IOException {
        final URL obj = uri.toURL();

        final HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Authorization", "Basic " + base64Credentials);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        final String postData = "scope=device_full_access&grant_type=client_credentials";
        final byte[] postDataBytes = postData.getBytes(StandardCharsets.UTF_8);

        try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
            wr.write(postDataBytes);
        }
        return connection;
    }

    private String readResponseFromServer(InputStream inputStream) throws IOException {
        final StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))) {
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        return response.toString();
    }
}
