package com.kryptokrauts.aeternity.sdk.service.transaction.domain;

import com.kryptokrauts.aeternity.generated.model.DryRunInput;
import com.kryptokrauts.aeternity.sdk.domain.GenericInputObject;
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

@Getter
@Builder
public class DryRunRequest extends GenericInputObject<DryRunInput> {

  private String block;

  @NonNull @Default private List<DryRunAccountModel> accounts = new LinkedList();

  @NonNull @Default private List<String> transactions = new LinkedList();

  public DryRunInput mapToModel() {
    return new DryRunInput()
        .top(block)
        .txs(transactions)
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
        validate -> Optional.ofNullable(this.transactions.size() > 0),
        this.accounts,
        "dryRunTransactions",
        Arrays.asList("unsignedTransactions"),
        ValidationUtil.NO_ENTRIES);
    ValidationUtil.checkParameters(
        validate -> Optional.ofNullable(this.transactions.size() == this.accounts.size()),
        this.accounts,
        "dryRunTransactions",
        Arrays.asList("unsignedTransactions", "accounts"),
        ValidationUtil.LIST_NOT_SAME_SIZE);
  }

  public DryRunRequest account(DryRunAccountModel account) {
    this.accounts.add(account);
    return this;
  }

  public DryRunRequest transaction(String unsignedTx) {
    this.transactions.add(unsignedTx);
    return this;
  }
}
