package com.scoreunit.rfb.screen;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class LoadingResourceTest {

	@Test
	public void test() throws IOException {
		
		assertNotNull(LoadingResource.get());
	}

}
