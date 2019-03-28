package com.ecfeed.core.webservice.client;

import java.net.Socket;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.X509KeyManager;

import com.ecfeed.junit.runner.SecurityHelper;
import com.ecfeed.junit.utils.Localization;
import com.ecfeed.junit.utils.Logger;

public final class ServiceRestKeyManager {
	
	private ServiceRestKeyManager() {
		RuntimeException exception = new RuntimeException(Localization.bundle.getString("classInitializationError"));
		Logger.exception(exception);
		throw exception;
	}
	
	static KeyManager[] noSecurity() {
		return null;
	}
	
	static KeyManager[] useKeyManagerCustom(String keyStorePath) {

		KeyManager[] certificates = new KeyManager[]{
				new X509KeyManager() {

					X509Certificate certificateClient = SecurityHelper.getCertificate(keyStorePath, SecurityHelper.ALIAS_CLIENT);
					X509Certificate certificateServer = SecurityHelper.getCertificate(keyStorePath, SecurityHelper.ALIAS_SERVER);
					PrivateKey privateKey = SecurityHelper.getPrivateKey(SecurityHelper.ALIAS_CLIENT, SecurityHelper.UNIVERSAL_PASSWORD);
					
					@Override
					public String chooseClientAlias(String[] arg0, Principal[] arg1, Socket arg2) {
						return SecurityHelper.TRUSTED_DOMAIN;
					}

					@Override
					public String chooseServerAlias(String arg0, Principal[] arg1, Socket arg2) {
						return null;
					}

					@Override
					public X509Certificate[] getCertificateChain(String arg0) {
						return new X509Certificate[] {certificateClient, certificateServer};
					}

					@Override
					public String[] getClientAliases(String arg0, Principal[] arg1) {
						return null;
					}

					@Override
					public PrivateKey getPrivateKey(String arg0) {
						return privateKey;
					}

					@Override
					public String[] getServerAliases(String arg0, Principal[] arg1) {
						return null;
					}
					
	            }
		};
		
		return certificates;
	}
	
	static KeyManager[] useKeyManager(String keyStorePath) {
		KeyManagerFactory keyManagerFactory = null;
		
		try {
			keyManagerFactory = KeyManagerFactory.getInstance("NewSunX509");
			keyManagerFactory.init(SecurityHelper.getKeyStore(keyStorePath), SecurityHelper.UNIVERSAL_PASSWORD.toCharArray());
		} catch (NoSuchAlgorithmException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceRestKeyManagerNoSuchAlgorithmException"), e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		} catch (UnrecoverableKeyException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceRestKeyManagerUnrecoverableKeyException"), e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		} catch (KeyStoreException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceRestKeyManagerKeyStoreException"), e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		}
       
        return keyManagerFactory.getKeyManagers();
	}

}
