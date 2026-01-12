package org.air_controller.http;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;

@Slf4j
public class HttpsGetRequest {
    public String sendRequest(String url) {
        String responseFromUrl = "";
        try {
            final URI uri = new URI(url);
            final HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("GET");

            final int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                responseFromUrl = readResponseFromServer(connection.getInputStream());
            }
        } catch (Exception exception) {
            log.error("HttpsGetRequest failure: ", exception);
            return "";
        }
        return responseFromUrl;
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
