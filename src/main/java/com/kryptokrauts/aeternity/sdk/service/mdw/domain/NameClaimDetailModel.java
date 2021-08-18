package com.kryptokrauts.aeternity.sdk.service.mdw.domain;

import com.kryptokrauts.aeternity.sdk.domain.GenericResultObject;
import com.kryptokrauts.mdw.generated.model.NameClaimDetail;
import java.math.BigInteger;
import java.util.List;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class NameClaimDetailModel
    extends GenericResultObject<NameClaimDetail, NameClaimDetailModel> {

  private BigInteger txIndex;
  private String txHash;
  private BigInteger txFee;
  private BigInteger blockHeight;
  private String microBlockHash;
  private BigInteger microBlockTime;
  private String claimer;
  private List<String> signatures;
  private String nameId;
  private String name;
  private BigInteger nameFee;
  private BigInteger nameSalt;

  @Override
  protected NameClaimDetailModel map(NameClaimDetail generatedResultObject) {
    if (generatedResultObject != null) {
      this.toBuilder()
          .txIndex(generatedResultObject.getTxIndex())
          .txHash(generatedResultObject.getHash())
          .txFee(generatedResultObject.getTx().getFee())
          .blockHeight(generatedResultObject.getBlockHeight())
          .microBlockHash(generatedResultObject.getBlockHash())
          .microBlockTime(generatedResultObject.getMicroTime())
          .claimer(generatedResultObject.getTx().getAccountId())
          .signatures(generatedResultObject.getSignatures())
          .nameId(generatedResultObject.getTx().getNameId())
          .name(generatedResultObject.getTx().getName())
          .nameFee(generatedResultObject.getTx().getNameFee())
          .nameSalt(generatedResultObject.getTx().getNameSalt())
          .build();
    }
    return this.toBuilder().build();
  }

  @Override
  protected String getResultObjectClassName() {
    return NameClaimDetailModel.class.getName();
  }
}
