package com.kryptokrauts.aeternity.sdk.service.transaction.domain;

import com.kryptokrauts.aeternity.generated.model.DryRunInputItem;
import com.kryptokrauts.aeternity.sdk.domain.GenericInputObject;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class DryRunInputItemModel extends GenericInputObject<DryRunInputItem> {

  private String tx;

  @Default private DryRunCallRequestModel callRequest = null;

  @Override
  public DryRunInputItem mapToModel() {
    return new DryRunInputItem()
        .tx(tx)
        .callReq(callRequest != null ? callRequest.toGeneratedModel() : null);
  }

  @Override
  protected void validate() {}
}
