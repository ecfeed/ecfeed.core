package com.ecfeed.core.webservice.client;

import com.ecfeed.core.utils.ExceptionHelper;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


public final class TrustManagerHelper {
	

	private TrustManagerHelper() { // TODO - remove ?
		ExceptionHelper.reportRuntimeException("Can not create.");
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
	
	static TrustManager[] createTrustManagerCustom(Optional<String> trustStorePath) {
	
		TrustManager[] trustManagers = new TrustManager[] {
			
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
					
					ExceptionHelper.reportRuntimeException("The server certificate is not trusted.");
	            }
	
				@Override
	            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	            }
	        }
		
		};
		
		return trustManagers;
	}
	
	static TrustManager[] useTrustManagerLocal(Optional<String> trustStorePath) {
		TrustManagerFactory trustManagerFactory = null;
		
		try {
			trustManagerFactory = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
			trustManagerFactory.init(SecurityHelper.getKeyStore(trustStorePath));
		} catch (NoSuchAlgorithmException e) {
			ExceptionHelper.reportRuntimeException("The algorithm for verifying the certificate could not be found (unknown algorithm).", e);
		} catch (NoSuchProviderException e) {
			ExceptionHelper.reportRuntimeException("The algorithm for verifying the certificate could not be found (unknown provider).", e);
		} catch (KeyStoreException e) {
			ExceptionHelper.reportRuntimeException("The trust store could not be loaded.", e);
		}

		return trustManagerFactory.getTrustManagers();			  
	}
	
	static TrustManager[] useTrustManagerGlobal() {
		TrustManagerFactory trustManagerFactory = null;
		
		try {
			trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init((KeyStore) null);
		} catch (NoSuchAlgorithmException e) {
			ExceptionHelper.reportRuntimeException("The algorithm for verifying the certificate could not be found (unknown algorithm).", e);
		} catch (KeyStoreException e) {
			ExceptionHelper.reportRuntimeException("The trust store could not be loaded", e);
		}

		return trustManagerFactory.getTrustManagers();			  
	}
	
}
