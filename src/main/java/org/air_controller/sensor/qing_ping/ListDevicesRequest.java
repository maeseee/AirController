package org.air_controller.sensor.qing_ping;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.zip.GZIPInputStream;

class ListDevicesRequest {
    private final URI uri;

    public ListDevicesRequest(URI uri) {
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
        final URI uriWithParameter = new URI(uri.toString() + "?timestamp=" + ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond());
        final HttpURLConnection connection = (HttpURLConnection) uriWithParameter.toURL().openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        return connection;
    }

    private String readResponseFromServer(InputStream inputStream) throws IOException {
        byte[] data;
        try (InputStream in = inputStream;
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            in.transferTo(buffer);
            data = buffer.toByteArray();
        }
        if (isGzipCompressed(data)) {
            return decompressDataFromGzip(data);
        }
        return new String(data, StandardCharsets.UTF_8);
    }

    private boolean isGzipCompressed(byte[] data) {
        return data.length >= 2 && (data[0] & 0xFF) == 0x1F && (data[1] & 0xFF) == 0x8B;
    }

    private String decompressDataFromGzip(byte[] data) throws IOException {
        try (InputStream gz = new GZIPInputStream(new ByteArrayInputStream(data));
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            gz.transferTo(out);
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        }
    }
}
