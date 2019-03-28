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

import com.ecfeed.junit.utils.Localization;
import com.ecfeed.junit.utils.Logger;
import com.google.common.io.BaseEncoding;

public final class SecurityHelper {

	private final static String[] DEFAULT_STORE_PATH = {
		"ecfeed/security",
		System.getProperty("user.home") + "/ecfeed/security",
		System.getProperty("java.home") + "/lib/security/cacerts"
	};
	
	public final static String UNIVERSAL_PASSWORD = "changeit";
	public final static String TRUSTED_DOMAIN = "ecfeed.com";
	
	public final static String ALIAS_CLIENT = "connection";
	public final static String ALIAS_SERVER = "ca";
	
	private final static String STORE_TYPE = "PKCS12";
	
	private static KeyStore loadedStore = null;
	private static String loadedStorePath = "";
	
	private SecurityHelper() {
		RuntimeException exception = new RuntimeException(Localization.bundle.getString("classInitializationError"));
		Logger.exception(exception);
		throw exception;
	}
	
	public static KeyStore getKeyStore() {

		if (loadedStore == null) {
			prepareStore(loadedStorePath);
		}

		return loadedStore;
	}
	
	public static KeyStore getKeyStore(String path) {
		
		if (path == null) {
			RuntimeException exception = new NullPointerException(Localization.bundle.getString("securityHelperMissingStorePath"));
			Logger.exception(exception);
			throw exception;
		}
		
		if (!loadedStorePath.equals(path)) {
			loadedStorePath = path;
			loadedStore = null;
		}
		
		return getKeyStore();
	}
	
	public static X509Certificate getCertificate(String keyStorePath, String alias) {
		
		if (alias == null) {
			RuntimeException exception = new NullPointerException(Localization.bundle.getString("securityHelperMissingCertificateAlias"));
			Logger.exception(exception);
			throw exception;
		}
		
		X509Certificate certificate = null;
		
		getKeyStore(keyStorePath);
		
		try {
			certificate = (X509Certificate) loadedStore.getCertificate(alias);
		} catch (KeyStoreException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("securityHelperKeystoreNotInitialized") + " " + alias, e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		}
		
		if (certificate == null) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("securityHelperErroneousCertificateAlias"));
			Logger.exception(exception);
			throw exception;
		}
		
		return certificate;
	}
	
	public static X509Certificate getCertificateFromFile(String path) {
		
		if (path == null) {
			RuntimeException exception = new NullPointerException(Localization.bundle.getString("securityHelperMissingCertificatePath"));
			Logger.exception(exception);
			throw exception;
		}
		
		X509Certificate certificate = null;
		
		getKeyStore();
		
		try {
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			certificate = (X509Certificate) certificateFactory.generateCertificate(Files.newInputStream(Paths.get(path)));
		} catch (IOException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("securityHelperCertificateIOException") + " " + path, e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		} catch (CertificateException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("securityHelperCertificateException") + " " + path, e);
			exception.addSuppressed(e);
			Logger.exception(exception);
			throw exception;
		}
		
		return certificate;
	}
	
	public static PublicKey getPublicKey(String keyStorePath, String alias) {
		
		if (alias == null) {
			RuntimeException exception = new NullPointerException(Localization.bundle.getString("securityHelperMissingPublicKeyAlias"));
			Logger.exception(exception);
			throw exception;
		}
		
		getKeyStore();
		
		return getCertificate(keyStorePath, alias).getPublicKey();		
	}
	
	public static PublicKey getPublicKeyFromFileOpenSSL(String path) {
		
		// https://stackoverflow.com/questions/47816938/java-ssh-rsa-string-to-public-key
		// https://github.com/jclouds/jclouds/blob/master/compute/src/main/java/org/jclouds/ssh/SshKeys.java
		
		if (path == null) {
			RuntimeException exception = new NullPointerException(Localization.bundle.getString("securityHelperMissingPublicKeyFilePath"));
			Logger.exception(exception);
			throw exception;
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
			 RuntimeException exception = new RuntimeException(Localization.bundle.getString("securityHelperPublicKeyIOException") + " " + path, e);
			 Logger.exception(exception);
			 exception.addSuppressed(e);
			 throw exception;
		 } catch (NoSuchAlgorithmException e) {
			 RuntimeException exception = new RuntimeException(Localization.bundle.getString("securityHelperPublicKeyNoSuchAlgorithmException"), e);
			 exception.addSuppressed(e);
			 Logger.exception(exception);
			 throw exception;
		 } catch (InvalidKeySpecException e) {
			 RuntimeException exception = new RuntimeException(Localization.bundle.getString("securityHelperPublicKeyInvalidKeySpecException"), e);
			 exception.addSuppressed(e);
			 Logger.exception(exception);
			 throw exception;
		 }
			  
		 return publicKey;
	}
	
	public static PrivateKey getPrivateKey(String alias, String password) {
		
		if (alias == null || password == null) {
			RuntimeException exception = new NullPointerException(Localization.bundle.getString("securityHelperMissingPrivateKeyAliasPassword"));
			Logger.exception(exception);
			throw exception;
		}
		
		PrivateKey privateKey = null;
		
		getKeyStore();
		
		try {
			char[] entryPassword = password.toCharArray();
			KeyStore.PasswordProtection entryProtection = new KeyStore.PasswordProtection(entryPassword);
			KeyStore.PrivateKeyEntry entryPrivateKey = (KeyStore.PrivateKeyEntry) loadedStore.getEntry(alias, entryProtection);
			privateKey = entryPrivateKey.getPrivateKey();
		} catch (KeyStoreException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("securityHelperPrivateKeyKeyStoreException") + " " + alias, e);
			Logger.exception(exception);
			exception.addSuppressed(e);
			throw exception;
		} catch (NoSuchAlgorithmException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("securityHelperPrivateKeyNoSuchAlgorithmException") + " " + alias, e);
			Logger.exception(exception);
			exception.addSuppressed(e);
			throw exception;
		} catch (UnrecoverableEntryException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("securityHelperPrivateKeyUnrecoverableEntryException") + " " + alias, e);
			Logger.exception(exception);
			exception.addSuppressed(e);
			throw exception;
		}
		
		return privateKey;
	}
	
	public static PrivateKey getPrivateKeyFromFilePKCS8(String path) {
		
		if (path == null) {
			RuntimeException exception = new NullPointerException(Localization.bundle.getString("securityHelperMissingPrivateKeyFileAlias"));
			Logger.exception(exception);
			throw exception;
		}
		
		PrivateKey privateKey = null;
		
		try {
			 byte[] byteArray = Files.readAllBytes(Paths.get(path));
			 
			 KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			 KeySpec keySpecification = new PKCS8EncodedKeySpec(byteArray);
			 privateKey = keyFactory.generatePrivate(keySpecification);
		} catch (NoSuchAlgorithmException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("securityHelperPrivateKeyFileNoSuchAlgorithmException"), e);
			Logger.exception(exception);
			exception.addSuppressed(e);
			throw exception;
		} catch (InvalidKeySpecException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("securityHelperPrivateKeyFileInvalidKeySpecException"), e);
			Logger.exception(exception);
			exception.addSuppressed(e);
			throw exception;
		} catch (IOException e) {
			RuntimeException exception = new RuntimeException(Localization.bundle.getString("securityHelperPrivateKeyFileIOException") + path, e);
			Logger.exception(exception);
			exception.addSuppressed(e);
			throw exception;
		}
		
		return privateKey;
	}
	
	private static void prepareStore(String path) throws IllegalArgumentException {

		if (path.equals("")) {
			loadedStore = prepareStoreLoad(prepareStoreUsingDefaultLocation());
		} else {
			loadedStore = prepareStoreLoad(prepareStoreUsingProvidedPath(path));
		}
		
	}
	
	private static Path prepareStoreUsingProvidedPath(String path) {
		
		Path storePath = Paths.get(path);
		Optional<String> storePathError = prepareStoreValidateFile(storePath);
		
		if (storePathError.isPresent()) {
			RuntimeException exception = new IllegalArgumentException(storePathError.get());
			Logger.exception(exception);
			throw exception;
		}
		
		return storePath;
	}
	
	private static Path prepareStoreUsingDefaultLocation() {
		
		for (String storePathChain : DEFAULT_STORE_PATH) {
			Path storePath = Paths.get(storePathChain);
			Optional<String> storePathError = prepareStoreValidateFile(storePath);
			
			if (storePathError.isPresent()) {
				continue;
			}
			
			return storePath;
		}

		RuntimeException exception = new IllegalArgumentException(Localization.bundle.getString("securityHelperLoadingStoreError") + " " + prepareStoreErrorMessage());
		Logger.exception(exception);
		throw exception;
	}
	
	private static Optional<String> prepareStoreValidateFile(Path path) {
		
		if (!Files.exists(path)) {
			return Optional.of(Localization.bundle.getString("securityHelperNonExistentFile") + " " + path.toAbsolutePath());
		}
		
		if (!Files.isReadable(path)) {
			return Optional.of(Localization.bundle.getString("securityHelperNotReadableFile") + " " + path.toAbsolutePath());
		}
		
		if (!Files.isRegularFile(path)) {
			return Optional.of(Localization.bundle.getString("securityHelperNotRegularFile") + " " + path.toAbsolutePath());
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
	
	private static KeyStore prepareStoreLoad(Path path) {
		KeyStore store = null;
		
		try {
			store = KeyStore.getInstance(STORE_TYPE);
		} catch (KeyStoreException e) {
			RuntimeException exception = new IllegalArgumentException(Localization.bundle.getString("securityHelperStoreKeyStoreException"), e);
			Logger.exception(exception);
			exception.addSuppressed(e);
			throw exception;
		}
		
		InputStream storeInputStream = null;
		
		try {
			storeInputStream = Files.newInputStream(path);
		} catch (IOException e) {
			RuntimeException exception = new IllegalArgumentException(Localization.bundle.getString("securityHelperCreateStoreError"), e);
			Logger.exception(exception);
			exception.addSuppressed(e);
			throw exception;
		}
		
		try {
			store.load(storeInputStream, UNIVERSAL_PASSWORD.toCharArray());
		} catch (NoSuchAlgorithmException e) {
			RuntimeException exception = new IllegalArgumentException(Localization.bundle.getString("securityHelperStoreNoSuchAlgorithmException"), e);
			Logger.exception(exception);
			exception.addSuppressed(e);
			throw exception;
		} catch (CertificateException e) {
			RuntimeException exception = new IllegalArgumentException(Localization.bundle.getString("securityHelperStoreCertificateException"), e);
			Logger.exception(exception);
			exception.addSuppressed(e);
			throw exception;
		} catch (IOException e) {
			RuntimeException exception = new IllegalArgumentException(Localization.bundle.getString("securityHelperStoreIOException") + path, e);
			Logger.exception(exception);
			exception.addSuppressed(e);
			throw exception;
		}
		
		loadedStorePath = path.toAbsolutePath().toString();
		
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
