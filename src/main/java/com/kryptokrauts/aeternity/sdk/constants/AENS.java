package com.kryptokrauts.aeternity.sdk.constants;

import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public interface AENS {

  BigInteger FEE_MULTIPLIER = new BigInteger("100000000000000");

  BigInteger MAX_TTL = new BigInteger("180000");

  BigInteger SMALLEST_FEE = new BigInteger("3").multiply(FEE_MULTIPLIER);

  String POINTER_KEY_ACCOUNT = "account_pubkey";
  String POINTER_KEY_CHANNEL = "channel";
  String POINTER_KEY_CONTRACT = "contract_pubkey";
  String POINTER_KEY_ORACLE = "oracle_pubkey";

  Map<Integer, BigInteger> INITIAL_NAME_LENGTH_FEE_MAP =
      new HashMap<Integer, BigInteger>() {
        {
          put(30, new BigInteger("5").multiply(FEE_MULTIPLIER));
          put(29, new BigInteger("8").multiply(FEE_MULTIPLIER));
          put(28, new BigInteger("13").multiply(FEE_MULTIPLIER));
          put(27, new BigInteger("21").multiply(FEE_MULTIPLIER));
          put(26, new BigInteger("34").multiply(FEE_MULTIPLIER));
          put(25, new BigInteger("55").multiply(FEE_MULTIPLIER));
          put(24, new BigInteger("89").multiply(FEE_MULTIPLIER));
          put(23, new BigInteger("144").multiply(FEE_MULTIPLIER));
          put(22, new BigInteger("233").multiply(FEE_MULTIPLIER));
          put(21, new BigInteger("377").multiply(FEE_MULTIPLIER));
          put(20, new BigInteger("610").multiply(FEE_MULTIPLIER));
          put(19, new BigInteger("987").multiply(FEE_MULTIPLIER));
          put(18, new BigInteger("1597").multiply(FEE_MULTIPLIER));
          put(17, new BigInteger("2584").multiply(FEE_MULTIPLIER));
          put(16, new BigInteger("4181").multiply(FEE_MULTIPLIER));
          put(15, new BigInteger("6765").multiply(FEE_MULTIPLIER));
          put(14, new BigInteger("10946").multiply(FEE_MULTIPLIER));
          put(13, new BigInteger("17711").multiply(FEE_MULTIPLIER));
          put(12, new BigInteger("28657").multiply(FEE_MULTIPLIER));
          put(11, new BigInteger("46368").multiply(FEE_MULTIPLIER));
          put(10, new BigInteger("75025").multiply(FEE_MULTIPLIER));
          put(9, new BigInteger("121393").multiply(FEE_MULTIPLIER));
          put(8, new BigInteger("196418").multiply(FEE_MULTIPLIER));
          put(7, new BigInteger("317811").multiply(FEE_MULTIPLIER));
          put(6, new BigInteger("514229").multiply(FEE_MULTIPLIER));
          put(5, new BigInteger("832040").multiply(FEE_MULTIPLIER));
          put(4, new BigInteger("1346269").multiply(FEE_MULTIPLIER));
          put(3, new BigInteger("2178309").multiply(FEE_MULTIPLIER));
          put(2, new BigInteger("3524578").multiply(FEE_MULTIPLIER));
          put(1, new BigInteger("5702887").multiply(FEE_MULTIPLIER));
        }
      };

  Map<String, String> POINTERKEY_TO_IDENTIFIER_MAP =
      new HashMap<String, String>() {
        {
          put(POINTER_KEY_ACCOUNT, ApiIdentifiers.ACCOUNT_PUBKEY);
          put(POINTER_KEY_CHANNEL, ApiIdentifiers.CHANNEL);
          put(POINTER_KEY_CONTRACT, ApiIdentifiers.CONTRACT_PUBKEY);
          put(POINTER_KEY_ORACLE, ApiIdentifiers.ORACLE_PUBKEY);
        }
      };

  /**
   * returns the time that is needed to wait until a claim becomes final for a given domain <br>
   * the avery keyblock-time is 3 minutes
   *
   * @param domain the domain including .chain
   * @return the timeout (in blocks) until the claim becomes final
   */
  static BigInteger getBlockTimeout(String domain) {
    int length = domain.split("\\.")[0].length();
    switch (length) {
      case 1:
      case 2:
      case 3:
      case 4:
        return BigInteger.valueOf(29760); // 2 months
      case 5:
      case 6:
      case 7:
      case 8:
        return BigInteger.valueOf(14880); // 1 month
      case 9:
      case 10:
      case 11:
      case 12:
        return BigInteger.valueOf(480); // 24 hours
      default:
        return BigInteger.ZERO;
    }
  }

  /**
   * returns the initial nameFee which is required to claim the name
   *
   * @param name the name to claim
   * @return the initial nameFee which is required to claim the name
   */
  static BigInteger getInitialNameFee(String name) {
    int length = name.split("\\.")[0].length();
    if (length >= 31) {
      return SMALLEST_FEE;
    }
    return INITIAL_NAME_LENGTH_FEE_MAP.get(length);
  }

  /**
   * returns the nameFee which is required for the next claim based on the current nameFee <br>
   * the next nameFee is 5% higher than the current nameFee
   *
   * @param currentNameFee the nameFee of the current claim
   * @return the nameFee which is required for the next claim
   */
  static BigInteger getNextNameFee(BigInteger currentNameFee) {
    BigDecimal nameFee = new BigDecimal(currentNameFee);
    return new BigDecimal(currentNameFee)
        .divide(BigDecimal.valueOf(100))
        .multiply(BigDecimal.valueOf(5))
        .add(nameFee)
        .setScale(2, RoundingMode.HALF_UP)
        .toBigInteger();
  }

  /**
   * @param name the AENS name (e.g. kryptokrauts.chain)
   * @return the nameId for a given name
   */
  static String getNameId(String name) {
    return EncodingUtils.encodeCheck(
        EncodingUtils.hash(name.toLowerCase().getBytes(StandardCharsets.UTF_8)),
        ApiIdentifiers.NAME);
  }
}
