package com.kryptokrauts.aeternity.sdk.domain.sophia;

import com.kryptokrauts.aeternity.sdk.domain.sophia.SophiaChainTTL.Type;
import com.kryptokrauts.aeternity.sdk.exception.InvalidParameterException;
import io.vertx.core.json.JsonObject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SophiaTypeTransformer {

  private static final List<Class<? extends SophiaType>> SUPPORTED_MAPPING_CLASSES =
      List.of(SophiaAENSName.class, SophiaAENSPointee.class, SophiaChainTTL.class);

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

  /**
   * @param decodedCompilerValue decoded value returned from the compiler. must NOT be a map or a
   *     list
   * @param sophiaTypeClass one of {@link SophiaType}, only {@link SophiaAENSName}, {@link
   *     SophiaAENSPointee} and {@link SophiaChainTTL} supported
   * @return the mapped result of the provided {@link SophiaType}
   */
  public static <T extends SophiaType> T getMappedResult(
      Object decodedCompilerValue, Class<T> sophiaTypeClass) {
    T mappedResult = null;
    if (!SUPPORTED_MAPPING_CLASSES.contains(sophiaTypeClass)) {
      throw new InvalidParameterException("Mapping for provided SophiaType not supported.");
    }
    JsonObject result = (JsonObject) decodedCompilerValue;
    if (SophiaAENSName.class.equals(sophiaTypeClass)) {
      List<Object> values = (List<Object>) result.getMap().get("AENS.Name");
      Map<String, List<Object>> ttlMap = (Map<String, List<Object>>) values.get(1);
      SophiaChainTTL chainTtl = getChainTtl(ttlMap);
      List<Object> pointees = (List<Object>) values.get(2);
      Map<String, SophiaAENSPointee> pointeeMap = new HashMap<>();
      for (int i = 0; i < pointees.size(); i++) {
        String key = ((List<Object>) pointees.get(i)).get(0).toString();
        Map<String, List<Object>> valueMap =
            ((Map<String, List<Object>>) ((List<Object>) pointees.get(i)).get(1));
        SophiaAENSPointee pointee = getAENSPointee(valueMap);
        pointeeMap.put(key, pointee);
      }
      mappedResult = (T) new SophiaAENSName(values.get(0).toString(), chainTtl, pointeeMap);
    } else if (SophiaAENSPointee.class.equals(sophiaTypeClass)) {
      Map valueMap = result.getMap();
      mappedResult = (T) getAENSPointee(valueMap);
    } else if (sophiaTypeClass.equals(SophiaChainTTL.class)) {
      Map ttlMap = result.getMap();
      mappedResult = (T) getChainTtl(ttlMap);
    }
    return mappedResult;
  }

  private static String transformParam(Object param) {
    if (param instanceof Map) {
      Set<String> mutationSet = new HashSet<>();
      ((Map<?, ?>) param)
          .forEach((k, v) -> mutationSet.add("[" + transformParam(k) + "] = " + transformParam(v)));
      return "{" + mutationSet.stream().collect(Collectors.joining(", ")) + "}";
    } else if (param instanceof List) {
      return ((List<?>) param)
          .stream().map(v -> transformParam(v)).collect(Collectors.toList()).toString();
    } else if (param instanceof Optional) {
      if (((Optional<?>) param).isEmpty()) {
        return "None";
      } else {
        return "Some(" + transformParam(((Optional<?>) param).get()) + ")";
      }
    } else if (param instanceof JsonObject) {
      return "{"
          + ((JsonObject) param)
              .getMap().entrySet().stream()
                  .map(e -> e.getKey() + "=" + transformParam(e.getValue()))
                  .collect(Collectors.joining(","))
          + "}";
    } else if (param instanceof SophiaType) {
      return ((SophiaType) param).getCompilerValue();
    }
    /**
     * works out of the box for other Sophia types: int (BigInteger / Integer), bool (Boolean),
     * address (String), ...
     */
    return param.toString();
  }

  private static SophiaAENSPointee getAENSPointee(Map<String, List<Object>> valueMap) {
    if (valueMap.containsKey("AENS.AccountPt")) {
      return new SophiaAENSPointee(valueMap.get("AENS.AccountPt").get(0).toString());
    } else if (valueMap.containsKey("AENS.ChannelPt")) {
      return new SophiaAENSPointee(
          valueMap.get("AENS.ChannelPt").get(0).toString().replace("ak_", "ch_"));
    } else if (valueMap.containsKey("AENS.ContractPt")) {
      return new SophiaAENSPointee(
          valueMap.get("AENS.ContractPt").get(0).toString().replace("ak_", "ct_"));
    } else if (valueMap.containsKey("AENS.OraclePt")) {
      return new SophiaAENSPointee(
          valueMap.get("AENS.OraclePt").get(0).toString().replace("ak_", "ok_"));
    }
    throw new InvalidParameterException("Mapping of Pointee not possible due to invalid param.");
  }

  private static SophiaChainTTL getChainTtl(Map<String, List<Object>> ttlMap) {
    Type ttlType;
    if (ttlMap.containsKey("FixedTTL")) {
      ttlType = Type.FixedTTL;
    } else {
      ttlType = Type.RelativeTTL;
    }
    BigInteger ttl = new BigInteger(ttlMap.get(ttlType.toString()).get(0).toString());
    return new SophiaChainTTL(ttl, ttlType);
  }
}
