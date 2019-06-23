package com.kryptokrauts.aeternity.sdk.util;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;

import com.kryptokrauts.aeternity.sdk.BaseTest;
import com.kryptokrauts.aeternity.sdk.exception.InvalidParameterException;
import org.junit.jupiter.api.Assertions;

public class ValidationUtilTest extends BaseTest {
  {
    describe(
        "AENS Naming System",
        () -> {
          it(
              "kryptokrauts.ae is not valid",
              () -> {
                String domain = "kryptokrauts.ae";
                Assertions.assertThrows(
                    InvalidParameterException.class, () -> ValidationUtil.checkNamespace(domain));
              });
          it(
              "kryptokrauts.test is valid",
              () -> {
                String domain = "kryptokrauts.test";
                Assertions.assertDoesNotThrow(() -> ValidationUtil.checkNamespace(domain));
              });
          it(
              "kryptokrauts is not valid",
              () -> {
                String domain = "kryptokrauts";
                Assertions.assertThrows(
                    InvalidParameterException.class, () -> ValidationUtil.checkNamespace(domain));
              });
        });
  }
}
