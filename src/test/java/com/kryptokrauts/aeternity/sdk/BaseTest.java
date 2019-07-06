package com.kryptokrauts.aeternity.sdk;

import java.math.BigInteger;

import org.junit.runner.RunWith;

import com.greghaskins.spectrum.Spectrum;

@RunWith(Spectrum.class)
public class BaseTest {

	public static final String defaultPassword = "kryptokrauts";

	public static final String DOMAIN = "kryptokrauts";

	public static final String NS = "test";

	public static final String KK_NAMESPACE = DOMAIN + "." + NS;

	public static final BigInteger TEST_SALT = new BigInteger("2654988072698203");
}
