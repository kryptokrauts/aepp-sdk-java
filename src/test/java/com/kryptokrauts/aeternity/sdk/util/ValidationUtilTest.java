package com.kryptokrauts.aeternity.sdk.util;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;

import com.kryptokrauts.aeternity.sdk.BaseTest;
import com.kryptokrauts.aeternity.sdk.exception.InvalidParameterException;
import org.junit.jupiter.api.Assertions;

public class ValidationUtilTest extends BaseTest {
  {
    String domainTooLong =
        "kryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokr.chain";
    String domainMaxAllowedLength =
        "kryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptokrautskryptok.chain";

    describe(
        "AENS Naming System",
        () -> {
          it(
              "kryptokrauts.eth is not valid",
              () -> {
                String domain = "kryptokrauts.eth";
                Assertions.assertThrows(
                    InvalidParameterException.class, () -> ValidationUtil.checkNamespace(domain));
              });
          it(
              "kryptokrauts.chain is valid",
              () -> {
                String domain = "kryptokrauts.chain";
                Assertions.assertDoesNotThrow(() -> ValidationUtil.checkNamespace(domain));
              });
          it(
              "kryptokrauts is not valid",
              () -> {
                String domain = "kryptokrauts";
                Assertions.assertThrows(
                    InvalidParameterException.class, () -> ValidationUtil.checkNamespace(domain));
              });
          it(
              "domain is too long",
              () -> {
                Assertions.assertThrows(
                    InvalidParameterException.class,
                    () -> ValidationUtil.checkNamespace(domainTooLong));
              });
          it(
              "domain length is valid",
              () -> {
                Assertions.assertDoesNotThrow(
                    () -> ValidationUtil.checkNamespace(domainMaxAllowedLength));
              });
        });
  }
}
