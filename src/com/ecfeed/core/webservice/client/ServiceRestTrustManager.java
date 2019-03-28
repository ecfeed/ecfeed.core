package com.ecfeed.core.webservice.client;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import com.ecfeed.junit.runner.SecurityHelper;
import com.ecfeed.junit.utils.Localization;
import com.ecfeed.junit.utils.Logger;

public final class ServiceRestTrustManager {
	

	private ServiceRestTrustManager() {
		RuntimeException exception = new RuntimeException(Localization.bundle.getString("classInitializationError"));
		Logger.exception(exception);
		throw exception;
	}
	
	static TrustManager[] noSecurity() {
		
		TrustManager[] certificates = new TrustManager[]{
				new X509TrustManager() {
					
					@Override
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}

					@Override
					public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	                }

					@Override
	                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	                }
	            }
		};
		
		return certificates;
	}
	
	static TrustManager[] useTrustManagerCustom(String trustStorePath) {
	
		TrustManager[] certificates = new TrustManager[]{
			
				new X509TrustManager() {
				
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
	
				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					List<X509TrustManager> trustManagerList = new ArrayList<>();
					
					trustManagerList.add((X509TrustManager) useTrustManagerLocal(trustStorePath)[0]);
					trustManagerList.add((X509TrustManager) useTrustManagerGlobal()[0]);
					
					for (X509TrustManager trustManager : trustManagerList) {
						
						try {
							trustManager.checkServerTrusted(chain, authType);
							return; 
					      } catch (CertificateException e) {
					      }
					    }
					
					RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceRestTrustManagerSignatureException"));
					Logger.exception(exception);
					throw exception;
	            }
	
				@Override
	            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	            }
	        }
		
		};
		
		return certificates;
	}
	
	static TrustManager[] useTrustManagerLocal(String trustStorePath) {
		TrustManagerFactory trustManagerFactory = null;
		
		try {
			trustManagerFactory = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
			trustManagerFactory.init(SecurityHelper.getKeyStore(trustStorePath));
		} catch (NoSuchAlgorithmException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceRestTrustManagerNoSuchAlgorithmException"), e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		} catch (NoSuchProviderException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceRestTrustManagerNoSuchProviderException"), e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		} catch (KeyStoreException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceRestTrustManagerKeyStoreException"), e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		}

		return trustManagerFactory.getTrustManagers();			  
	}
	
	static TrustManager[] useTrustManagerGlobal() {
		TrustManagerFactory trustManagerFactory = null;
		
		try {
			trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init((KeyStore) null);
		} catch (NoSuchAlgorithmException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceRestTrustManagerNoSuchAlgorithmException"), e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		} catch (KeyStoreException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("serviceRestTrustManagerKeyStoreException"), e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		}

		return trustManagerFactory.getTrustManagers();			  
	}
	
}
