package com.kryptokrauts.aeternity.sdk.util;

import com.kryptokrauts.aeternity.sdk.exception.InvalidParameterException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ValidationUtil {

  public static final int NAME_MAX_LENGHT = 253;

  public static final String LIST_NOT_SAME_SIZE = "Lists don't have the same size";

  public static final String NO_ENTRIES = "List or map has no entries";

  public static final String NAMESPACE_INVALID = "Namespace not allowed / not provided.";

  public static final String PARAMETER_IS_NULL = "Parameter cannot be null.";

  public static final String MISSING_API_IDENTIFIER =
      "Parameter does not start with expected APIIdentifier.";

  public static final String NAMESPACE_EXCEEDS_LIMIT =
      String.format("Domainname exceeds %s char limit.", NAME_MAX_LENGHT);

  public static final List<String> ALLOWED_NAMESPACES = Arrays.asList("chain");

  public static final String INVALID_POINTER = "Pointer is not valid.";

  public static final String DUPLICATE_POINTER_KEY =
      "For each pointer key only one value may be provided. The list of pointers contains duplicate value-types.";

  public static final String NAME_FEE_TOO_LOW =
      "The provided nameFee is too low. Minimum '%s' is required.";

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
   * validate the given name
   *
   * @param name the AENS name to validate
   */
  public static void checkNamespace(String name) {
    String[] nameSplit = name.split("\\.");

    checkParameters(
        validate ->
            Optional.ofNullable(nameSplit.length == 2 && ALLOWED_NAMESPACES.contains(nameSplit[1])),
        name,
        "checkNamespace",
        Arrays.asList("name"),
        ValidationUtil.NAMESPACE_INVALID);

    checkParameters(
        validate -> Optional.ofNullable(name.length() <= NAME_MAX_LENGHT),
        name,
        "checkNamespace",
        Arrays.asList("name"),
        ValidationUtil.NAMESPACE_EXCEEDS_LIMIT);
  }
}
