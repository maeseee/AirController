package org.airController.sensor.qingPing;

import org.airController.secrets.Secret;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

public class QingPingAccessToken {
    static final String APP_KEY = "me8h7AKSR";
    static final String ENVIRONMENT_VARIABLE_APP_SECRET = "qingping_app_secret";
    static final String ENCRYPTED_APP_SECRET = "P2Yg64Btliolc1DDvQFQKYZAb2ufYF10khTLrGfrb9d2kM1tA8ciYhZ2bbQeHdOLlIGmSfM4JQcG6EcnYtvm8w==";

    private final QingPingAccessTokenRequest accessTokenRequest;
    private final QingPingAccessTokenJsonParser parser = new QingPingAccessTokenJsonParser();

    private String token;
    private LocalDateTime accessTokenValidUntil;

    public QingPingAccessToken() throws URISyntaxException {
        this(createAccessTokenRequest());
    }

    QingPingAccessToken(QingPingAccessTokenRequest accessTokenRequest) {
        this.accessTokenRequest = accessTokenRequest;
    }

    public String getToken() throws CommunicationException {
        if (accessTokenValidUntil == null || accessTokenValidUntil.isBefore(LocalDateTime.now())) {
            updateAccessToken();
        }
        return token;
    }

    private void updateAccessToken() throws CommunicationException {
        final Optional<String> request = accessTokenRequest.sendRequest();
        if (request.isEmpty()) {
            throw new CommunicationException("QingPing access token could not be updated");
        }

        final Optional<QingPingAccessTokenData> accessTokenOptional = parser.parse(request.get());
        if (accessTokenOptional.isPresent()) {
            final QingPingAccessTokenData accessTokenData = accessTokenOptional.get();
            token = accessTokenData.accessToken();
            accessTokenValidUntil = LocalDateTime.now().plusSeconds(accessTokenData.expiresIn() - 60);
        }
    }

    private static QingPingAccessTokenRequest createAccessTokenRequest() throws URISyntaxException {
        final String credentials = getCredentialsForPostRequest();
        final String urlString = "https://oauth.cleargrass.com/oauth2/token";
        final URI uri = new URI(urlString);
        return new QingPingAccessTokenRequest(uri, credentials);
    }

    private static String getCredentialsForPostRequest() {
        final String appSecret = Secret.getSecret(ENVIRONMENT_VARIABLE_APP_SECRET, ENCRYPTED_APP_SECRET);
        final String credentials = APP_KEY + ":" + appSecret;
        return Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }
}
