package com.kryptokrauts.aeternity.sdk.domain.sophia;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SophiaTypeTransformer {

  /**
   * This method can be used to explicitly transform a list of params ({@link SophiaType}) into a
   * representation the Sophia compiler expects it.
   *
   * @param params a list params. for objects of type {@link SophiaType} as well as {@link
   *     java.util.Map}, {@link List} and {@link java.util.Optional} that embed a {@link SophiaType}
   *     this method will be able to transform the params automatically. for all other objects you
   *     need to make sure that their toString()-method provides the expected transformation
   * @return the params in the form the Sophia compiler expects it to be
   */
  public static List<String> toCompilerInput(List<Object> params) {
    if (params == null) {
      return null;
    }
    List<String> compilerInputParams = new ArrayList<>();
    params.forEach(p -> compilerInputParams.add(transformParam(p)));
    return compilerInputParams;
  }

  private static String transformParam(Object param) {
    if (param instanceof Map) {
      Set<String> mutationSet = new HashSet<>();
      ((Map<?, ?>) param)
          .forEach((k, v) -> mutationSet.add("[" + transformParam(k) + "] = " + transformParam(v)));
      return "{" + mutationSet.stream().collect(Collectors.joining(", ")) + "}";
    } else if (param instanceof List) {
      // TODO
      return "";
    } else if (param instanceof Optional) {
      // TODO
      return "";
    } else if (param instanceof SophiaType) {
      return ((SophiaType) param).getSophiaValue();
    }
    /**
     * works out of the box for Sophia types: int (BigInteger / Integer), bool (Boolean), address
     * (String), ...
     */
    return param.toString();
  }
}
