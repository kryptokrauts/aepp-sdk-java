package com.kryptokrauts.aeternity.sdk.service.transaction.domain;

import com.kryptokrauts.aeternity.generated.model.DryRunInput;
import com.kryptokrauts.aeternity.sdk.domain.GenericInputObject;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.AbstractTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ContractCreateTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.ValidationUtil;
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

  public DryRunRequest transactionInputItem(DryRunInputItemModel inputModel) {
    this.txInputs.add(inputModel);
    return this;
  }

  public DryRunRequest transactionInputItem(String unsignedTx) {
    this.txInputs.add(DryRunInputItemModel.builder().tx(unsignedTx).build());
    return this;
  }

  public DryRunRequest transactionInputItem(
      AbstractTransactionModel<?> inputTx, String unsignedTx) {
    if (inputTx instanceof ContractCreateTransactionModel) {
      ContractCreateTransactionModel cctm = (ContractCreateTransactionModel) inputTx;
      this.txInputs.add(
          DryRunInputItemModel.builder()
              .tx(unsignedTx)
              .callRequest(
                  DryRunCallRequestModel.builder()
                      .amount(cctm.getAmount())
                      .calldata(cctm.getCallData())
                      .gas(cctm.getGas())
                      .contract(cctm.getContractByteCode())
                      .caller(cctm.getOwnerId())
                      .abiVersion(cctm.getVirtualMachine().getAbiVersion())
                      .nonce(cctm.getNonce())
                      .build())
              .build());
    }
    return this;
  }
}
