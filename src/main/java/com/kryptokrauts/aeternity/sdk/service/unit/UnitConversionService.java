package com.kryptokrauts.aeternity.sdk.service.unit;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * this interface provides methods to convert between the biggest and smallest unit of a token
 * according to the given unit-factor.
 *
 * <p>when communicating with the protocol it's always necessary to use the value of the smallest
 * unit.
 *
 * <p>the default-implementation {@link
 * com.kryptokrauts.aeternity.sdk.service.unit.impl.DefaultUnitConversionServiceImpl} provides a
 * constructor where the unit-factor can be provided. by default the factor to use for the biggest
 * unit is 18
 */
public interface UnitConversionService {

  BigDecimal toBiggestUnit(BigInteger valueOfSmallestUnit);

  BigDecimal toBiggestUnit(String valueOfSmallestUnit);

  BigInteger toSmallestUnit(String valueOfBiggestUnit);

  BigInteger toSmallestUnit(BigDecimal valueOfBiggestUnit);
}
