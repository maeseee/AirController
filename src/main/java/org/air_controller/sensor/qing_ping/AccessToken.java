package org.air_controller.sensor.qing_ping;

import org.air_controller.secrets.Secret;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Optional;

class AccessToken {
    static final String APP_KEY = "me8h7AKSR";
    static final String ENVIRONMENT_VARIABLE_APP_SECRET = "qingping_app_secret";
    static final String ENCRYPTED_APP_SECRET = "P2Yg64Btliolc1DDvQFQKYZAb2ufYF10khTLrGfrb9d2kM1tA8ciYhZ2bbQeHdOLlIGmSfM4JQcG6EcnYtvm8w==";

    private final AccessTokenRequest accessTokenRequest;
    private final AccessTokenJsonParser parser = new AccessTokenJsonParser();

    private String token;
    private ZonedDateTime accessTokenValidUntil;

    public AccessToken() throws URISyntaxException {
        this(createAccessTokenRequest());
    }

    AccessToken(AccessTokenRequest accessTokenRequest) {
        this.accessTokenRequest = accessTokenRequest;
    }

    public String readToken() throws CommunicationException {
        if (accessTokenValidUntil == null || accessTokenValidUntil.isBefore(ZonedDateTime.now(ZoneOffset.UTC))) {
            updateAccessToken();
        }
        return token;
    }

    private void updateAccessToken() throws CommunicationException {
        final Optional<String> request = accessTokenRequest.sendRequest();
        if (request.isEmpty()) {
            throw new CommunicationException("QingPing access token could not be updated");
        }

        final Optional<AccessTokenData> accessTokenOptional = parser.parse(request.get());
        if (accessTokenOptional.isPresent()) {
            final AccessTokenData accessTokenData = accessTokenOptional.get();
            token = accessTokenData.accessToken();
            accessTokenValidUntil = ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(accessTokenData.expiresIn() - 60L);
        }
    }

    private static AccessTokenRequest createAccessTokenRequest() throws URISyntaxException {
        final String credentials = getCredentialsForPostRequest();
        final String urlString = "https://oauth.cleargrass.com/oauth2/token";
        final URI uri = new URI(urlString);
        return new AccessTokenRequest(uri, credentials);
    }

    private static String getCredentialsForPostRequest() {
        final String appSecret = Secret.getSecret(ENVIRONMENT_VARIABLE_APP_SECRET, ENCRYPTED_APP_SECRET);
        final String credentials = APP_KEY + ":" + appSecret;
        return Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }
}
