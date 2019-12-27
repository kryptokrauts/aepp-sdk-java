package com.kryptokrauts.aeternity.sdk.service.unit;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface UnitConversionService {

  BigDecimal toBiggestUnit(BigInteger valueOfSmallestUnit);

  BigDecimal toBiggestUnit(String valueOfSmallestUnit);

  BigInteger toSmallestUnit(String valueOfBiggestUnit);

  BigInteger toSmallestUnit(BigDecimal valueOfBiggestUnit);
}
