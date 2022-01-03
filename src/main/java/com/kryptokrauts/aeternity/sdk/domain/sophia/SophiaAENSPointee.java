package com.kryptokrauts.aeternity.sdk.domain.sophia;

import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.exception.InvalidParameterException;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public class SophiaAENSPointee extends SophiaType {

  private String address;
  private Type type;

  public SophiaAENSPointee(String target) {
    String type = target.substring(0, 2);
    if (!ApiIdentifiers.ACCOUNT_PUBKEY.equals(type)
        && !ApiIdentifiers.CHANNEL.equals(type)
        && !ApiIdentifiers.CONTRACT_PUBKEY.equals(type)
        && !ApiIdentifiers.ORACLE_PUBKEY.equals(type)) {
      throw new InvalidParameterException(
          String.format("Type %s of given target is not allowed", type));
    }
    if (!EncodingUtils.hasValidType(target, type)) {
      throw new InvalidParameterException(
          String.format("Given target %s is not a valid aeternity type", target));
    }
    this.address = target.replace(type + "_", "ak_");
    switch (type) {
      case ApiIdentifiers.ACCOUNT_PUBKEY:
        this.type = Type.AccountPt;
        break;
      case ApiIdentifiers.CONTRACT_PUBKEY:
        this.type = Type.ContractPt;
        break;
      case ApiIdentifiers.CHANNEL:
        this.type = Type.ChannelPt;
        break;
      case ApiIdentifiers.ORACLE_PUBKEY:
        this.type = Type.OraclePt;
        break;
    }
  }

  public String getAddress() {
    return this.address;
  }

  public Type getType() {
    return this.type;
  }

  @Override
  public String getCompilerValue() {
    return "AENS." + this.getType() + "(" + this.address + ")";
  }

  public enum Type {
    AccountPt,
    OraclePt,
    ContractPt,
    ChannelPt
  }
}
