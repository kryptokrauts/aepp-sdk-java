package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.GenericTx;
import com.kryptokrauts.aeternity.generated.model.NamePointer;
import com.kryptokrauts.aeternity.generated.model.NameUpdateTx;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.service.name.domain.NamePointerModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.NameUpdateTransaction;
import com.kryptokrauts.aeternity.sdk.util.ValidationUtil;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
public class NameUpdateTransactionModel extends AbstractTransactionModel<NameUpdateTx> {

  private String accountId;
  private BigInteger nonce;
  private String nameId;
  private BigInteger ttl;
  private BigInteger nameTtl;
  private BigInteger clientTtl;

  @Default private List<NamePointerModel> pointers = new LinkedList<NamePointerModel>();

  @Override
  public NameUpdateTx toApiModel() {
    NameUpdateTx nameUpdateTx = new NameUpdateTx();
    nameUpdateTx.setAccountId(this.accountId);
    nameUpdateTx.setNonce(this.nonce);
    nameUpdateTx.setNameId(nameId);
    nameUpdateTx.setFee(this.fee);
    nameUpdateTx.setTtl(this.ttl);
    nameUpdateTx.setNameTtl(nameTtl);
    nameUpdateTx.setClientTtl(clientTtl);
    nameUpdateTx.setPointers(getGeneratedPointers());
    return nameUpdateTx;
  }

  public List<NamePointer> getGeneratedPointers() {
    return pointers.stream()
        .map(pointer -> new NamePointer().id(pointer.getId()).key(pointer.getKey()))
        .collect(Collectors.toList());
  }

  @Override
  public Function<GenericTx, NameUpdateTransactionModel> getApiToModelFunction() {
    return (tx) -> {
      NameUpdateTx castedTx = (NameUpdateTx) tx;
      return this.toBuilder()
          .accountId(castedTx.getAccountId())
          .fee(castedTx.getFee())
          .nonce(castedTx.getNonce())
          .nameId(castedTx.getNameId())
          .nameTtl(castedTx.getNameTtl())
          .clientTtl(castedTx.getClientTtl())
          .ttl(castedTx.getTtl())
          .pointers(
              castedTx.getPointers().stream()
                  .map(
                      pointer ->
                          NamePointerModel.builder()
                              .id(pointer.getId())
                              .key(pointer.getKey())
                              .build())
                  .collect(Collectors.toList()))
          .build();
    };
  }

  @Override
  public void validateInput() {
    // Validate parameters
    ValidationUtil.checkParameters(
        validate -> Optional.ofNullable(nameId != null),
        nameId,
        "validateUpdateTransaction",
        Arrays.asList("nameId"),
        ValidationUtil.PARAMETER_IS_NULL);
    ValidationUtil.checkParameters(
        validate -> Optional.ofNullable(nameId.startsWith(ApiIdentifiers.NAME)),
        nameId,
        "validateUpdateTransaction",
        Arrays.asList("nameId", ApiIdentifiers.NAME),
        ValidationUtil.MISSING_API_IDENTIFIER);
  }

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
    return NameUpdateTransaction.builder().externalApi(externalApi).model(this).build();
  }
}
