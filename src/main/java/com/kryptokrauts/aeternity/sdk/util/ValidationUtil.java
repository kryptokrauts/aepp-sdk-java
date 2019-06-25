package com.kryptokrauts.aeternity.sdk.util;

import com.kryptokrauts.aeternity.sdk.exception.InvalidParameterException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ValidationUtil {

  public static final String LIST_NOT_SAME_SIZE = "Lists don't have the same size";

  public static final String NO_ENTRIES = "List or map has no entries";

  public static final String MAP_MISSING_VALUE = "Map is missing value for %s";

  public static final List<String> ALLOWED_NAMESPACES = Arrays.asList("test");

  public static final int DOMAIN_NAME_MAX_LENGHT = 253;

  /**
   * encapsule validation of given parameters
   *
   * @param validationMethod the validation method to apply on the object, which should return an
   *     optional of boolean
   * @param objectToValidate the object to validate
   * @param methodName the method, where the validation takes places
   * @param parameters the parameter(s) which are validated
   * @param cause optional message for detailled explanation of the validation error
   */
  public static void checkParameters(
      Function<Object, Optional<Boolean>> validationMethod,
      Object objectToValidate,
      String methodName,
      List<String> parameters,
      Object... cause) {
    if (validationMethod.apply(objectToValidate).orElseGet(() -> Boolean.FALSE).booleanValue()
        == false) {
      String causeMessage = "";
      if (cause != null) {
        if (cause.length > 1) {
          Object[] paramArray = Arrays.copyOfRange(cause, 1, cause.length);
          causeMessage = ": " + String.format(cause[0].toString(), paramArray);
        } else {
          causeMessage = ": " + cause[0].toString();
        }
      }
      throw new InvalidParameterException(
          String.format(
              "Call of function %s has missing or invalid parameters %s%s",
              methodName, parameters, causeMessage));
    }
  }

  /**
   * validate the given domainName
   *
   * @param domainName
   */
  public static void checkNamespace(String domainName) {
    String[] domainSplit = domainName.split("\\.");
    boolean isValid = domainSplit.length == 2 && ALLOWED_NAMESPACES.contains(domainSplit[1]);
    if (!isValid) {
      throw new InvalidParameterException("Namespace not allowed / not provided.");
    } else if (domainName.length() > DOMAIN_NAME_MAX_LENGHT) {
      throw new InvalidParameterException(
          String.format("Domainname exceeds %s char limit.", DOMAIN_NAME_MAX_LENGHT));
    }
  }
}
