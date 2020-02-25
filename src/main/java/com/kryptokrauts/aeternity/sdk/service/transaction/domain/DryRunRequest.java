package com.kryptokrauts.aeternity.sdk.service.transaction.domain;

import com.kryptokrauts.aeternity.generated.model.DryRunInput;
import com.kryptokrauts.aeternity.sdk.constants.BaseConstants;
import com.kryptokrauts.aeternity.sdk.domain.GenericInputObject;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ContractCallTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.ValidationUtil;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@Builder
@ToString
public class DryRunRequest extends GenericInputObject<DryRunInput> {

  private String block;

  @NonNull @Default private List<DryRunAccountModel> accounts = new LinkedList<>();

  @NonNull @Default private List<DryRunInputItemModel> txInputs = new LinkedList<>();

  public DryRunInput mapToModel() {
    return new DryRunInput()
        .top(block)
        .txs(txInputs.stream().map(input -> input.toGeneratedModel()).collect(Collectors.toList()))
        .accounts(
            accounts.stream()
                .map(account -> account.toGeneratedModel())
                .collect(Collectors.toList()));
  }

  @Override
  protected void validate() {
    ValidationUtil.checkParameters(
        validate -> Optional.ofNullable(this.accounts.size() > 0),
        this.accounts,
        "dryRunTransactions",
        Arrays.asList("accounts"),
        ValidationUtil.NO_ENTRIES);
    ValidationUtil.checkParameters(
        validate -> Optional.ofNullable(this.txInputs.size() > 0),
        this.accounts,
        "dryRunTransactions",
        Arrays.asList("unsignedTransactions"),
        ValidationUtil.NO_ENTRIES);
    ValidationUtil.checkParameters(
        validate -> Optional.ofNullable(this.txInputs.size() == this.accounts.size()),
        this.accounts,
        "dryRunTransactions",
        Arrays.asList("unsignedTransactions", "accounts"),
        ValidationUtil.LIST_NOT_SAME_SIZE);
  }

  public DryRunRequest account(DryRunAccountModel account) {
    this.accounts.add(account);
    return this;
  }

  /**
   * Add transaction input item using custom model
   *
   * @param inputModel instance of {@link DryRunInputItemModel}
   * @return instance of {@link DryRunRequest}
   */
  public DryRunRequest transactionInputItem(DryRunInputItemModel inputModel) {
    this.txInputs.add(inputModel);
    return this;
  }

  /**
   * Add transaction input item using unsigned tx string
   *
   * @param unsignedTx unsigned tx string
   * @return instance of {@link DryRunRequest}
   */
  public DryRunRequest transactionInputItem(String unsignedTx) {
    this.txInputs.add(DryRunInputItemModel.builder().tx(unsignedTx).build());
    return this;
  }

  /**
   * Add transaction input item using contract call model
   *
   * @param contractCallModel instance of {@link ContractCallTransactionModel}
   * @return instance of {@link DryRunRequest}
   */
  public DryRunRequest transactionInputItem(ContractCallTransactionModel contractCallModel) {
    if (contractCallModel != null) {
      this.txInputs.add(
          DryRunInputItemModel.builder()
              .callRequest(
                  DryRunCallRequestModel.builder()
                      .amount(contractCallModel.getAmount())
                      .calldata(contractCallModel.getCallData())
                      .caller(contractCallModel.getCallerId())
                      .contract(contractCallModel.getContractId())
                      .gas(BigInteger.valueOf(BaseConstants.MINIMAL_GAS_PRICE))
                      .abiVersion(contractCallModel.getVirtualMachine().getAbiVersion())
                      .nonce(contractCallModel.getNonce())
                      .build())
              .build());
    }
    return this;
  }
}
