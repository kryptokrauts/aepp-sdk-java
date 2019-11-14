package com.kryptokrauts.aeternity.sdk;

import com.greghaskins.spectrum.Spectrum;
import java.math.BigInteger;
import org.junit.runner.RunWith;

@RunWith(Spectrum.class)
public class BaseTest {

  public static final String defaultPassword = "kryptokrauts";

  public static final String NAME = "kryptokrauts";

  public static final String NS = "test";

  public static final String KK_NAMESPACE = NAME + "." + NS;

  public static final BigInteger TEST_SALT = new BigInteger("2654988072698203");
}
