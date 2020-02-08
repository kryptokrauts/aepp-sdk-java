package com.kryptokrauts.aeternity.sdk.service.unit.impl;

import com.kryptokrauts.aeternity.sdk.service.unit.UnitConversionService;
import java.math.BigDecimal;
import java.math.BigInteger;

public class DefaultUnitConversionServiceImpl implements UnitConversionService {

  private BigDecimal biggestUnitFactor;

  public DefaultUnitConversionServiceImpl() {
    this.biggestUnitFactor = BigDecimal.TEN.pow(18);
  }

  public DefaultUnitConversionServiceImpl(int tokenDecimals) {
    this.biggestUnitFactor = BigDecimal.TEN.pow(tokenDecimals);
  }

  @Override
  public BigDecimal toBiggestUnit(BigInteger valueOfSmallestUnit) {
    return new BigDecimal(valueOfSmallestUnit).divide(biggestUnitFactor);
  }

  @Override
  public BigDecimal toBiggestUnit(String valueOfSmallestUnit) {
    return new BigDecimal(valueOfSmallestUnit).divide(biggestUnitFactor);
  }

  @Override
  public BigInteger toSmallestUnit(String valueOfBiggestUnit) {
    return new BigDecimal(valueOfBiggestUnit).multiply(biggestUnitFactor).toBigInteger();
  }

  @Override
  public BigInteger toSmallestUnit(BigDecimal valueOfBiggestUnit) {
    return valueOfBiggestUnit.multiply(biggestUnitFactor).toBigInteger();
  }
}
