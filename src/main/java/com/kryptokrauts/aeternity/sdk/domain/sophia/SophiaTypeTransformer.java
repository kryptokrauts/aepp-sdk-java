package com.kryptokrauts.aeternity.sdk.domain.sophia;

import com.kryptokrauts.aeternity.sdk.domain.sophia.SophiaChainTTL.Type;
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

  public static <T extends SophiaType> T getMappedResult(
      Object decodedCompilerValue, Class<T> sophiaType) {
    T mappedResult = null;
    if (sophiaType.equals(SophiaAENSName.class)) {
      JsonObject result = (JsonObject) decodedCompilerValue;
      List<Object> values = (List<Object>) result.getMap().get("AENS.Name");
      Map<String, List<Object>> ttlMap = (Map<String, List<Object>>) values.get(1);
      Type ttlType;
      if (ttlMap.containsKey("FixedTTL")) {
        ttlType = Type.FixedTTL;
      } else {
        ttlType = Type.RelativeTTL;
      }
      BigInteger ttl = new BigInteger(ttlMap.get(ttlType.toString()).get(0).toString());
      SophiaChainTTL chainTtl = new SophiaChainTTL(ttl, ttlType);
      List<Object> pointees = (List<Object>) values.get(2);
      Map<String, SophiaAENSPointee> pointeeMap = new HashMap<>();
      for (int i = 0; i < pointees.size(); i++) {
        String key = ((List<Object>) pointees.get(i)).get(0).toString();
        String value = null;
        Map<String, List<Object>> valueMap =
            ((Map<String, List<Object>>) ((List<Object>) pointees.get(i)).get(1));
        if (valueMap.containsKey("AENS.AccountPt")) {
          value = valueMap.get("AENS.AccountPt").get(0).toString();
        } else if (valueMap.containsKey("AENS.ChannelPt")) {
          value = valueMap.get("AENS.ChannelPt").get(0).toString().replace("ak_", "ch_");
        } else if (valueMap.containsKey("AENS.ContractPt")) {
          value = valueMap.get("AENS.ContractPt").get(0).toString().replace("ak_", "ct_");
        } else if (valueMap.containsKey("AENS.OraclePt")) {
          value = valueMap.get("AENS.OraclePt").get(0).toString().replace("ak_", "ok_");
        }
        pointeeMap.put(key, new SophiaAENSPointee(value));
      }
      mappedResult = (T) new SophiaAENSName(values.get(0).toString(), chainTtl, pointeeMap);
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
      // TODO
      return "";
    } else if (param instanceof SophiaType) {
      return ((SophiaType) param).getCompilerValue();
    }
    /**
     * works out of the box for Sophia types: int (BigInteger / Integer), bool (Boolean), address
     * (String), ...
     */
    return param.toString();
  }
}
