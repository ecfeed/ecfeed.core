package com.ecfeed.core.webservice.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Optional;

import com.ecfeed.core.utils.ExceptionHelper;
import com.google.common.io.BaseEncoding;

import static com.ecfeed.core.utils.ExceptionHelper.reportRuntimeExceptionCanNotCreateObject;

public final class SecurityHelper {

	private final static String[] DEFAULT_STORE_PATH = {
		"./.ecfeed/security.p12",
		System.getProperty("user.home") + "/.ecfeed/security.p12",
		System.getProperty("java.home") + "/lib/security/cacerts"
	};
	
	public final static String UNIVERSAL_PASSWORD = "changeit";
	public final static String TRUSTED_DOMAIN = "ecfeed.com";
	
	public final static String ALIAS_CLIENT = "connection";
	public final static String ALIAS_SERVER = "ca";
	
	private final static String STORE_TYPE = "PKCS12";
	
	private static KeyStore fLoadedStore = null;
	private static Optional<String> fLoadedStorePath = Optional.empty();
	
	private SecurityHelper() { // TODO - remove ?
		reportRuntimeExceptionCanNotCreateObject();
	}
	
	public static KeyStore getKeyStore() {

		if (fLoadedStore == null) {
			loadKeyStoreFromPath(fLoadedStorePath); // TODO - static method returning fLoadedStore
		}

		return fLoadedStore;
	}
	
	public static KeyStore getKeyStore(Optional<String> path) {
		
		if (path == null) {
			ExceptionHelper.reportRuntimeException("The path to the store must be provided.");
		}
		
		if (!fLoadedStorePath.equals(path)) {
			fLoadedStorePath = path;
			fLoadedStore = null;
		}
		
		return getKeyStore();
	}
	
	public static X509Certificate getCertificate(Optional<String> keyStorePath, String alias) {
		
		if (alias == null) {
			ExceptionHelper.reportRuntimeException("The certificate alias must be provided");
		}
		
		X509Certificate certificate = null;
		
		getKeyStore(keyStorePath);
		
		try {
			certificate = (X509Certificate) fLoadedStore.getCertificate(alias);
		} catch (KeyStoreException e) {
			ExceptionHelper.reportRuntimeException("The store was not initialized: " + alias, e);
		}
		
		if (certificate == null) {
			ExceptionHelper.reportRuntimeException("The certificate with the requested alias is not in the store.");
		}
		
		return certificate;
	}
	
	public static X509Certificate getCertificateFromFile(String path) {
		
		if (path == null) {
			ExceptionHelper.reportRuntimeException("The path to the certificate must be provided.");
		}
		
		X509Certificate certificate = null;
		
		getKeyStore();
		
		try {
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			certificate = (X509Certificate) certificateFactory.generateCertificate(Files.newInputStream(Paths.get(path)));
		} catch (IOException e) {
			ExceptionHelper.reportRuntimeException("The requested certificate could not be read from file: " + path, e);
		} catch (CertificateException e) {
			ExceptionHelper.reportRuntimeException("The format of the requested certificate is invalid: " + path, e);
		}
		
		return certificate;
	}
	
	public static PublicKey getPublicKey(Optional<String> keyStorePath, String alias) {
		
		if (alias == null) {
			ExceptionHelper.reportRuntimeException("The public key alias must be provided.");
		}
		
		getKeyStore();
		
		return getCertificate(keyStorePath, alias).getPublicKey();		
	}
	
	public static PublicKey getPublicKeyFromFileOpenSSL(String path) {
		
		// https://stackoverflow.com/questions/47816938/java-ssh-rsa-string-to-public-key
		// https://github.com/jclouds/jclouds/blob/master/compute/src/main/java/org/jclouds/ssh/SshKeys.java
		
		if (path == null) {
			ExceptionHelper.reportRuntimeException("The path to the public key must be provided.");
		}
		
		PublicKey publicKey = null;
		 
		 try {
			 byte[] byteArray = Files.readAllBytes(Paths.get(path));
	    	
			 String[] segments = new String(byteArray, "UTF-8").split(" ");
			 InputStream byteStream = new ByteArrayInputStream(BaseEncoding.base64().decode(segments[1]));
   
			 getPublicKeyReadParameter(byteStream);
			 BigInteger publicExponent = getPublicKeyReadParameter(byteStream);
			 BigInteger modulus = getPublicKeyReadParameter(byteStream);
	      
			 RSAPublicKeySpec keySpecification = new RSAPublicKeySpec(modulus, publicExponent);
			  
			 KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			 
			 publicKey = keyFactory.generatePublic(keySpecification);
		 } catch (IOException e) {
		 	 ExceptionHelper.reportRuntimeException("The requested public key could not be read from file: " + path, e);
		 } catch (NoSuchAlgorithmException e) {
		 	 ExceptionHelper.reportRuntimeException("The requested public key could not be read." + e);
		 } catch (InvalidKeySpecException e) {
		 	 ExceptionHelper.reportRuntimeException("The format of the requested public key is invalid.", e);
		 }
			  
		 return publicKey;
	}
	
	public static PrivateKey getPrivateKey(String alias, String password) {
		
		if (alias == null || password == null) {
			ExceptionHelper.reportRuntimeException("The path and password to the private key must be provided.");
		}
		
		PrivateKey privateKey = null;
		
		getKeyStore();
		
		try {
			char[] entryPassword = password.toCharArray();
			KeyStore.PasswordProtection entryProtection = new KeyStore.PasswordProtection(entryPassword);
			KeyStore.PrivateKeyEntry entryPrivateKey = (KeyStore.PrivateKeyEntry) fLoadedStore.getEntry(alias, entryProtection);
			privateKey = entryPrivateKey.getPrivateKey();
		} catch (KeyStoreException e) {
			ExceptionHelper.reportRuntimeException("The password associated with the requested key is erroneous: " + alias + ".", e);
		} catch (NoSuchAlgorithmException e) {
			ExceptionHelper.reportRuntimeException("The algorithm for recovering the private key could not be found: " + alias, e);
		} catch (UnrecoverableEntryException e) {
			ExceptionHelper.reportRuntimeException("Not enough information to recover the key: " + alias, e);
		}
		
		return privateKey;
	}
	
	public static PrivateKey getPrivateKeyFromFilePKCS8(String path) {
		
		if (path == null) {
			ExceptionHelper.reportRuntimeException("The path to the public key must be provided.");
		}
		
		PrivateKey privateKey = null;
		
		try {
			 byte[] byteArray = Files.readAllBytes(Paths.get(path));
			 
			 KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			 KeySpec keySpecification = new PKCS8EncodedKeySpec(byteArray);
			 privateKey = keyFactory.generatePrivate(keySpecification);
		} catch (NoSuchAlgorithmException e) {
			ExceptionHelper.reportRuntimeException("The algorithm for recovering the private key could not be found.", e);
		} catch (InvalidKeySpecException e) {
			ExceptionHelper.reportRuntimeException("The format of the requested private key is invalid.", e);
		} catch (IOException e) {
			ExceptionHelper.reportRuntimeException("The requested public key could not be read from file: " + path, e);
		}
		
		return privateKey;
	}
	
	private static void loadKeyStoreFromPath(Optional<String> path) throws IllegalArgumentException {

		if (path.isPresent()) {
		    if (path.get().equals("")) {
		        fLoadedStore = prepareLoadedStore(prepareStoreUsingDefaultLocation());
            } else {
                fLoadedStore = prepareLoadedStore(prepareStoreUsingProvidedPath(path.get()));
            }
		} else {
			fLoadedStore = prepareLoadedStore(prepareStoreUsingDefaultLocation());
		}
	}
	
	private static Path prepareStoreUsingProvidedPath(String path) {
		
		Path storePath = Paths.get(path);
		Optional<String> storePathError = validateStorePath(storePath);
		
		if (storePathError.isPresent()) {
			ExceptionHelper.reportRuntimeException("Illegal argument. " + storePathError.get());
		}
		
		return storePath;
	}
	
	private static Path prepareStoreUsingDefaultLocation() {
		
		for (String storePathChain : DEFAULT_STORE_PATH) {
			Path storePath = Paths.get(storePathChain);
			Optional<String> storePathError = validateStorePath(storePath);
			
			if (storePathError.isPresent()) {
				continue;
			}
			
			return storePath;
		}

		ExceptionHelper.reportRuntimeException("The required store could not be loaded. Please provide a valid path or use one of the following locations: " + prepareStoreErrorMessage());
		return null;
	}
	
	private static Optional<String> validateStorePath(Path storePath) {
		
		if (!Files.exists(storePath)) {
			return Optional.of("The file does not exist: " + storePath.toAbsolutePath());
		}
		
		if (!Files.isReadable(storePath)) {
			return Optional.of("The file is not readable: " + storePath.toAbsolutePath());
		}
		
		if (!Files.isRegularFile(storePath)) {
			return Optional.of( "The type of the file is erroneous: " + storePath.toAbsolutePath());
		}
		
		return Optional.empty();
	}
	
	private static String prepareStoreErrorMessage() {
		StringBuffer storePathChainError = new StringBuffer();
		
		for (String storePathChain : DEFAULT_STORE_PATH) {
			storePathChainError.append(System.lineSeparator());
			storePathChainError.append(storePathChain);
		}
		
		return storePathChainError.toString();
	}
	
	private static KeyStore prepareLoadedStore(Path path) {
		KeyStore store = null;
		
		try {
			store = KeyStore.getInstance(STORE_TYPE);
		} catch (KeyStoreException e) {
			ExceptionHelper.reportRuntimeException("The store could not be created.", e);
		}
		
		InputStream storeInputStream = null;
		
		try {
			storeInputStream = Files.newInputStream(path);
		} catch (IOException e) {
			ExceptionHelper.reportRuntimeException("The store could not be created.", e);
		}
		
		try {
			store.load(storeInputStream, UNIVERSAL_PASSWORD.toCharArray());
		} catch (NoSuchAlgorithmException e) {
			ExceptionHelper.reportRuntimeException("The algorithm for checking the store integrity could not be found.", e);
		} catch (CertificateException e) {
			ExceptionHelper.reportRuntimeException("At least one of the certificates included in the store could not be loaded.", e);
		} catch (IOException e) {
			ExceptionHelper.reportRuntimeException("The password is incorrect. Store path: " + path, e);
		}
		
		fLoadedStorePath = Optional.of(path.toAbsolutePath().toString());
		
		return store;
	}
	
	private static BigInteger getPublicKeyReadParameter(InputStream in) throws IOException {
		int length = 0;
		
		length += in.read() << 24;
		length += in.read() << 16;
		length += in.read() << 8;
		length += in.read() << 0;
		
		byte[] value = new byte[length];
		in.read(value, 0, length);
		
		return new BigInteger(value);
	}
		
}
