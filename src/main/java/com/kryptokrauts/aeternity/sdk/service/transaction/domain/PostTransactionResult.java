package com.kryptokrauts.aeternity.sdk.service.transaction.domain;

import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.sdk.domain.GenericResultObject;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@ToString
public class PostTransactionResult
    extends GenericResultObject<PostTxResponse, PostTransactionResult> {

  private String txHash;

  @Override
  protected PostTransactionResult map(PostTxResponse generatedResultObject) {
    if (generatedResultObject != null)
      return PostTransactionResult.builder().txHash(generatedResultObject.getTxHash()).build();
    else return PostTransactionResult.builder().build();
  }

  @Override
  protected String getResultObjectClassName() {
    return PostTransactionResult.class.getName();
  }
}
