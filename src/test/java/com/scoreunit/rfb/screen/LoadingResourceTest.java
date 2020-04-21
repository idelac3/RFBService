package com.scoreunit.rfb.screen;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoadingResourceTest {

	@Test
	public void test_01_loadResource() throws IOException {
		
		int w = 640, h = 480;
		
		assertNotNull(LoadingResource.get(w, h));
	}


	@Test(expected = IOException.class)
	public void test_02_loadResourceNonExisting() throws IOException {
		
		int w = 640, h = 480;
		
		LoadingResource.RESOURCE_FILENAME = "blabla.123";
		assertNotNull(LoadingResource.get(w, h));
	}
	
	@AfterClass
	public static void restore() {
		
		LoadingResource.RESOURCE_FILENAME = "git.properties";
	}
}
