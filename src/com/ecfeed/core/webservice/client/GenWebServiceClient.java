package com.ecfeed.core.webservice.client;

import com.ecfeed.junit.utils.Localization;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class GenWebServiceClient implements IWebServiceClient {

    static final private String TAG_CLIENT_VERSION = "clientVersion";
    static final private String TAG_CLIENT_TYPE = "clientType";
    static final private String TAG_REQUEST_TYPE = "requestType";

    private Client fClient;
    private String fClientType;
    private String fClientVersion;
    private WebTarget fWebTarget;

    public GenWebServiceClient(
            String targetStr,
            String communicationProtocol,
            String keyStorePath,
            String clientType,
            String clientVersion) {

        fClientType = clientType;
        fClientVersion = clientVersion;

        fClient = createClient(communicationProtocol, keyStorePath);
        fWebTarget = fClient.target(targetStr);
    }

    @Override
    public WebServiceResponse postRequest(
            String requestType, String requestJson) {

        Response response = fWebTarget
                .queryParam(TAG_CLIENT_TYPE, fClientType)
                .queryParam(TAG_CLIENT_VERSION, fClientVersion)
                .queryParam(TAG_REQUEST_TYPE, requestType)
                .request()
                .post(Entity.entity(requestJson, MediaType.APPLICATION_JSON));

        int responseStatus = response.getStatus();

        BufferedReader responseBufferedReader =
                new BufferedReader(new InputStreamReader(response.readEntity(InputStream.class)));

        return new WebServiceResponse(responseStatus, responseBufferedReader);
    }

    @Override
    public void close() {

        if (fClient != null) {
            fClient.close();
        }
    }

    private static Client createClient(String communicationProtocol, String keyStorePath) {

        ClientBuilder client = ClientBuilder.newBuilder();

        client.hostnameVerifier(ServiceWebHostnameVerifier.noSecurity());
        client.sslContext(createSslContext(communicationProtocol, keyStorePath));

        return client.build();
    }

    private static SSLContext createSslContext(String communicationProtocol, String keyStorePath) {

        SSLContext securityContext = null;

        try {
            securityContext = SSLContext.getInstance(communicationProtocol);
            securityContext.init(ServiceRestKeyManager.useKeyManagerCustom(keyStorePath), ServiceRestTrustManager.useTrustManagerCustom(keyStorePath), new SecureRandom());

        } catch (KeyManagementException e) {

            RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceRestSecureConnectionError"), e);
            exception.addSuppressed(e);
            throw exception;

        } catch (NoSuchAlgorithmException e) {
            RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceRestNoProtocolProvider"), e);
            exception.addSuppressed(e);
            throw exception;
        }

        return securityContext;
    }

}
