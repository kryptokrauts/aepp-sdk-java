package com.kryptokrauts.aeternity.sdk.util;

import com.kryptokrauts.aeternity.sdk.util.UnitConversionUtil.Unit;
import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;

public class UnitConversionUtilTest {

  @Test
  public void toAettos() {
    Assert.assertEquals(
        new BigDecimal("1000000000000000000"), UnitConversionUtil.toAettos("1", Unit.AE));
  }

  @Test
  public void fromAettos() {
    Assert.assertEquals(
        BigDecimal.ONE, UnitConversionUtil.fromAettos("1000000000000000000", Unit.AE));
  }
}
