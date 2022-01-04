package com.kryptokrauts.aeternity.sdk.util;

import com.kryptokrauts.aeternity.sdk.exception.InvalidParameterException;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.AbstractTransactionModel;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/** Realizes the {@link AbstractTransactionModel}s input values validation */
public class ValidationUtil {

  public static final int NAME_MAX_LENGTH = 253;

  public static final String LIST_NOT_SAME_SIZE = "Lists don't have the same size";

  public static final String NO_ENTRIES = "List or map has no entries";

  public static final String NAMESPACE_INVALID = "Namespace not allowed / not provided.";

  public static final String PARAMETER_IS_NULL = "Parameter cannot be null.";

  public static final String MISSING_API_IDENTIFIER =
      "Parameter does not start with expected APIIdentifier.";

  public static final String NAMESPACE_EXCEEDS_LIMIT =
      String.format("Domainname exceeds %s char limit.", NAME_MAX_LENGTH);

  public static final List<String> ALLOWED_NAMESPACES = Arrays.asList("chain");

  public static final String INVALID_STANDARD_POINTER = "Invalid value for a default pointer key.";

  public static final String POINTER_LIMIT_EXCEEDED = "Exceeded the limit of 32 pointers.";

  public static final String NAME_FEE_TOO_LOW =
      "The provided nameFee is too low. Minimum '%s' is required.";

  /**
   * encapsule validation of given parameters
   *
   * @param validationMethod the validation method to apply on the object, which should return a
   *     boolean
   * @param objectToValidate the object to validate
   * @param methodName the method, where the validation takes places
   * @param parameters the parameter(s) which are validated
   * @param cause optional message for detailed explanation of the validation error
   */
  public static void checkParameters(
      Function<Object, Boolean> validationMethod,
      Object objectToValidate,
      String methodName,
      List<String> parameters,
      Object... cause) {
    if (validationMethod.apply(objectToValidate) == false) {
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
        validate -> nameSplit.length == 2 && ALLOWED_NAMESPACES.contains(nameSplit[1]),
        name,
        "checkNamespace",
        Arrays.asList("name"),
        ValidationUtil.NAMESPACE_INVALID);

    checkParameters(
        validate -> name.length() <= NAME_MAX_LENGTH,
        name,
        "checkNamespace",
        Arrays.asList("name"),
        ValidationUtil.NAMESPACE_EXCEEDS_LIMIT);
  }
}
