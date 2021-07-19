package com.kryptokrauts.aeternity.sdk.service.info.domain;

import com.kryptokrauts.aeternity.generated.model.SignedTxs;
import com.kryptokrauts.aeternity.sdk.domain.GenericResultObject;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class TransactionResults extends GenericResultObject<SignedTxs, TransactionResults> {

  private List<TransactionResult> results;

  @Override
  protected TransactionResults map(SignedTxs generatedResultObject) {
    if (generatedResultObject != null)
      return this.toBuilder()
          .results(
              generatedResultObject.getTransactions().stream()
                  .map(tx -> TransactionResult.builder().build().map(tx))
                  .collect(Collectors.toList()))
          .build();
    else return this.toBuilder().build();
  }

  @Override
  protected String getResultObjectClassName() {
    return this.getClass().getName();
  }
}
