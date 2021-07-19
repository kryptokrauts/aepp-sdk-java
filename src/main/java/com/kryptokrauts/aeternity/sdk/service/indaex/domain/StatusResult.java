package com.kryptokrauts.aeternity.sdk.service.indaex.domain;

import com.kryptokrauts.aeternity.sdk.domain.GenericResultObject;
import com.kryptokrauts.indaex.generated.model.Status;
import java.math.BigDecimal;
import java.math.BigInteger;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class StatusResult extends GenericResultObject<Status, StatusResult> {

  private BigInteger mdwHeight;
  private Boolean mdwSynced;
  private BigInteger mdwTxIndex;
  private String mdwVersion;
  private BigInteger nodeHeight;
  private BigDecimal nodeProgress;
  private Boolean nodeSyncing;
  private String nodeVersion;

  @Override
  protected StatusResult map(Status generatedResultObject) {
    if (generatedResultObject != null) {
      return this.toBuilder()
          .mdwHeight(generatedResultObject.getMdwHeight())
          .mdwSynced(generatedResultObject.getMdwSynced())
          .mdwTxIndex(generatedResultObject.getMdwTxIndex())
          .mdwVersion(generatedResultObject.getMdwVersion())
          .nodeHeight(generatedResultObject.getNodeHeight())
          .nodeProgress(generatedResultObject.getNodeProgress())
          .nodeSyncing(generatedResultObject.getNodeSyncing())
          .nodeVersion(generatedResultObject.getNodeVersion())
          .build();
    } else {
      return this.toBuilder().build();
    }
  }

  @Override
  protected String getResultObjectClassName() {
    return StatusResult.class.getName();
  }
}
