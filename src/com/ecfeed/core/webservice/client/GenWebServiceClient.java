package com.ecfeed.core.webservice.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.ecfeed.core.utils.ExceptionHelper;
import com.ecfeed.core.utils.UrlHelper;

public class GenWebServiceClient implements IWebServiceClient {

	private static final String TAG_CLIENT_VERSION = "clientVersion";
	private static final String TAG_CLIENT_TYPE = "clientType";
	private static final String TAG_REQUEST_TYPE = "requestType";
	private static final String fCommunicationProtocol = "TLSv1.2";
	private static final String fClientVersion = "1.0";

	private Client fClient;
	private String fClientType;
	private WebTarget fWebTarget;


	public GenWebServiceClient(
			String serverUrl,
			String endpoint,
			String clientType,
			Optional<String> keyStorePath) {

		fClientType = clientType;

		fClient = createClient(fCommunicationProtocol, keyStorePath);

		String targetStr = UrlHelper.merge(serverUrl, endpoint);

		fWebTarget = fClient.target(targetStr);
	}

	public static String getTestCasesEndPoint() {
		return "testCaseService";
	}

	public static String getTestCasesJsonEndPoint() {
		return "testCaseServiceJson";
	}

	public static String getGenServiceVersionEndPoint() {
		return "genServiceVersion";
	}

	@Override
	public WebServiceResponse sendPostRequest(
			String requestType, String requestJson) {

		Response response = fWebTarget
				.queryParam(TAG_CLIENT_TYPE, fClientType)
				.queryParam(TAG_CLIENT_VERSION, fClientVersion)
				.queryParam(TAG_REQUEST_TYPE, requestType)
				.request()
				.post(Entity.entity(requestJson, MediaType.APPLICATION_JSON));

		int responseStatus = response.getStatus();

//		String responseText = response.readEntity(String.class);

		BufferedReader responseBufferedReader =
				new BufferedReader(new InputStreamReader(response.readEntity(InputStream.class)));

		return new WebServiceResponse(responseStatus, responseBufferedReader);
	}

	@Override
	public WebServiceResponse sendGetRequest() {

		Response response = fWebTarget
				//				.queryParam(TAG_CLIENT_TYPE, fClientType)
				//				.queryParam(TAG_CLIENT_VERSION, fClientVersion)
				//.queryParam(TAG_REQUEST_TYPE, requestType)
				.request()
				.get();

		int responseStatus = response.getStatus();

		BufferedReader responseBufferedReader =
				new BufferedReader(new InputStreamReader(response.readEntity(InputStream.class)));

		return new WebServiceResponse(responseStatus, responseBufferedReader);
	}

	@Override
	public void close() {

		if (fClient != null) {
			fClient.close();
//			System.out.println(LocalDateTime.now().toString() + " Remote connection closed");
			fClient = null;
		}
	}

	private static Client createClient(String communicationProtocol, Optional<String> keyStorePath) {

		ClientBuilder client = ClientBuilder.newBuilder();

		client.hostnameVerifier(ServiceWebHostnameVerifier.noSecurity());
		client.sslContext(createSslContext(communicationProtocol, keyStorePath));

		return client.build();
	}

	private static SSLContext createSslContext(
			String communicationProtocol,
			Optional<String> keyStorePath) {

		SSLContext securityContext = null;

		try {
			securityContext = SSLContext.getInstance(communicationProtocol);
			securityContext.init(
					KeyManagerHelper.useKeyManagerCustom(keyStorePath), 
					TrustManagerHelper.createTrustManagerCustom(keyStorePath), new SecureRandom());

		} catch (KeyManagementException e) {

			ExceptionHelper.reportRuntimeException("The secure connection (TLSv1.2) could not be established.", e);

		} catch (NoSuchAlgorithmException e) {

			ExceptionHelper.reportRuntimeException("The implementation for the protocol specified is not available", e);
		}

		return securityContext;
	}

}
