package com.scoreunit.rfb.ssl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLServerSocketFactory;

import org.junit.Test;

public class SSLUtilTest {

	@Test
	public void test_01_jks() throws KeyManagementException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		
		final String resourceFileName = "store-example1.jks";
		final String password = "blabla123";
		
		final InputStream keystoreFile = SSLUtilTest.class.getClassLoader().getResourceAsStream(resourceFileName);
		
		final SSLServerSocketFactory factory = SSLUtil.newInstance(SSLUtil.KEYSTORE_TYPE_JKS, keystoreFile, password);
		assertNotNull(factory);
	}
	
	@Test
	public void test_02_pkcs12() throws KeyManagementException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		
		final String resourceFileName = "key-example1.pfx";
		final String password = "blabla123";
		
		final InputStream keystoreFile = SSLUtilTest.class.getClassLoader().getResourceAsStream(resourceFileName);
		
		final SSLServerSocketFactory factory = SSLUtil.newInstance(SSLUtil.KEYSTORE_TYPE_PKCS12, keystoreFile, password);
		assertNotNull(factory);
	}

}
