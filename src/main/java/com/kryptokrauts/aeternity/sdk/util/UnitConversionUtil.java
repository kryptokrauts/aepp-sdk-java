package com.kryptokrauts.aeternity.sdk.util;

import java.math.BigDecimal;

/**
 * This class can be used to convert AE to and from different units. code adapted from
 * https://github.com/web3j/web3j/blob/master/utils/src/main/java/org/web3j/utils/Convert.java
 *
 * <p>We recommend to use the {@link
 * com.kryptokrauts.aeternity.sdk.service.unit.UnitConversionService} - especially for converting
 * units of FungibleTokens - instead
 */
public final class UnitConversionUtil {
  private UnitConversionUtil() {}

  public static BigDecimal fromAettos(String number, Unit unit) {
    return fromAettos(new BigDecimal(number), unit);
  }

  public static BigDecimal fromAettos(BigDecimal number, Unit unit) {
    return number.divide(unit.getAettosFactor());
  }

  public static BigDecimal toAettos(String number, Unit unit) {
    return toAettos(new BigDecimal(number), unit);
  }

  public static BigDecimal toAettos(BigDecimal number, Unit unit) {
    return number.multiply(unit.getAettosFactor());
  }

  public enum Unit {
    AETTOS("ættos", 0),
    AE("AE", 18);

    private String name;
    private BigDecimal aettosFactor;

    Unit(String name, int factor) {
      this.name = name;
      this.aettosFactor = BigDecimal.TEN.pow(factor);
    }

    public BigDecimal getAettosFactor() {
      return aettosFactor;
    }

    @Override
    public String toString() {
      return name;
    }

    public static Unit fromString(String name) {
      if (name != null) {
        for (Unit unit : Unit.values()) {
          if (name.equalsIgnoreCase(unit.name)) {
            return unit;
          }
        }
      }
      return Unit.valueOf(name);
    }
  }
}
