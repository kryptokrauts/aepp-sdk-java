package com.kryptokrauts.aeternity.sdk.service.unit;

import com.greghaskins.spectrum.Spectrum;
import com.kryptokrauts.aeternity.sdk.BaseTest;
import com.kryptokrauts.aeternity.sdk.service.unit.impl.DefaultUnitConversionServiceImpl;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;

public class UnitConversionServiceTest extends BaseTest {
  {
    final UnitConversionService unitConversionService = new DefaultUnitConversionServiceImpl();
    final UnitConversionService unitConversionServiceWithTenDecimals =
        new DefaultUnitConversionServiceImpl(10);

    Spectrum.describe(
        "test default constructor with 18 decimals",
        () -> {
          String biggestValueString = "1.333333333333333337";
          BigDecimal biggestValue = new BigDecimal(biggestValueString);
          String smallestValueString = "1333333333333333337";
          BigInteger smallestValue = new BigInteger(smallestValueString);
          Spectrum.it(
              "toBiggestUnit works",
              () -> {
                Assertions.assertEquals(
                    biggestValue, unitConversionService.toBiggestUnit(smallestValue));
                Assertions.assertEquals(
                    biggestValue, unitConversionService.toBiggestUnit(smallestValueString));
              });
          Spectrum.it(
              "toSmallestUnit works",
              () -> {
                Assertions.assertEquals(
                    smallestValue, unitConversionService.toSmallestUnit(biggestValue));
                Assertions.assertEquals(
                    smallestValue, unitConversionService.toSmallestUnit(biggestValueString));
              });
        });
    Spectrum.describe(
        "test constructor with 10 decimals",
        () -> {
          String biggestValueString = "1.3333333337";
          BigDecimal biggestValue = new BigDecimal(biggestValueString);
          String smallestValueString = "13333333337";
          BigInteger smallestValue = new BigInteger(smallestValueString);
          Spectrum.it(
              "toBiggestUnit works",
              () -> {
                Assertions.assertEquals(
                    biggestValue,
                    unitConversionServiceWithTenDecimals.toBiggestUnit(smallestValue));
                Assertions.assertEquals(
                    biggestValue,
                    unitConversionServiceWithTenDecimals.toBiggestUnit(smallestValueString));
              });
          Spectrum.it(
              "toSmallestUnit works",
              () -> {
                Assertions.assertEquals(
                    smallestValue,
                    unitConversionServiceWithTenDecimals.toSmallestUnit(biggestValue));
                Assertions.assertEquals(
                    smallestValue,
                    unitConversionServiceWithTenDecimals.toSmallestUnit(biggestValueString));
              });
        });
  }
}
