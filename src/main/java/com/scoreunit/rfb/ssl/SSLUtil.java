package com.scoreunit.rfb.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * Helper method(s) to enable SSL secure communcation.
 * <p>
 * To generate private key and certificate (self-signed), use <i>openssl</i> utility.
 * <pre>
 * openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 365 -subj '/CN=localhost' -nodes
 * openssl pkcs12 -export -inkey key.pem -in cert.pem -out final_result.pfx
 * </pre>
 * First command will build <i>key.pem</i> file, and certificate <i>cert.pem</i> with example CN attribute set to <i>localhost</i>.
 * <p>
 * Second command will bundle key and certificate into PKCS12 format, in this example into <i>final_result.pfx</i> file.
 * <p>
 * If Java <i>keytool</i> program is available, it is possible to generate JKS key store.
 * <pre>
 * keytool -genkeypair -keyalg rsa -keysize 4096 -keystore mystore.jks -storepass blabla123 -storetype jks -v
 * </pre>
 * In this example chosen key store file name is <i>mystore.jks</i> with password <i>blabla123</i> and default validity period of 90 days.
 * 
 * @author igor.delac@gmail.com
 *
 */
public class SSLUtil {

	/**
	 * Some commonly available or supported key store types, by Java.
	 * <p>
	 * Ref.
	 *  <a href="https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyStore">
	 *  KeyStore Types</a>
	 */
	final public static String KEYSTORE_TYPE_JKS = "JKS"
			, KEYSTORE_TYPE_PKCS12 = "PKCS12";
	
	/**
	 * Create instance of {@link SSLServerSocketFactory} used to create secure SSL sockets.
	 * 
	 * @param keystoreType		-	type of key store, either {@link #KEYSTORE_TYPE_PKCS12} or {@link #KEYSTORE_TYPE_JKS}
	 * @param keystoreFile		-	input PKCS12 file, usually <i>*.pfx</i> or <i>*.p12</i> files
	 * @param password			-	password that was used to generate PKCS12 file
	 * 
	 * @return	instance of {@link SSLServerSocketFactory}
	 * 
	 * @throws KeyManagementException		if {@link SSLContext#init(javax.net.ssl.KeyManager[], TrustManager[], java.security.SecureRandom)} operation fails
	 * @throws KeyStoreException			if no Provider supports a KeyStoreSpi implementation for the specified type
	 * @throws UnrecoverableKeyException	if the key cannot be recovered (e.g. the given password is wrong).
	 * @throws NoSuchAlgorithmException		if the algorithm used to check the integrity of the keystore cannot be found
	 * @throws CertificateException			if any of the certificates in the keystore could not be loaded
	 * @throws IOException					if there is an I/O or format problem with the keystore data, if a password is required but not given, or if the given password was incorrect. If the error is due to a wrong password
	 */
	public static SSLServerSocketFactory newInstance(final String keystoreType
			, final InputStream keystoreFile, final String password) throws KeyManagementException, KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, IOException {
		
		final KeyStore keyStore = KeyStore.getInstance(keystoreType);
		keyStore.load(keystoreFile, password.toCharArray());

		final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyManagerFactory.init(keyStore, password.toCharArray());

		final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()); 
		trustManagerFactory.init(keyStore);

		final SSLContext sslContext = SSLContext.getInstance("TLS"); 
		final TrustManager[] trustManagers = trustManagerFactory.getTrustManagers(); 
		sslContext.init(keyManagerFactory.getKeyManagers(), trustManagers, null); 

		final SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
		
		return sslServerSocketFactory;
	}
}
