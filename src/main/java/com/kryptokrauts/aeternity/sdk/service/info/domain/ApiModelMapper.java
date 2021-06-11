package com.kryptokrauts.aeternity.sdk.service.info.domain;

import com.kryptokrauts.aeternity.generated.model.ChannelCloseMutualTx;
import com.kryptokrauts.aeternity.generated.model.ChannelCloseSoloTx;
import com.kryptokrauts.aeternity.generated.model.ChannelCreateTx;
import com.kryptokrauts.aeternity.generated.model.ChannelDepositTx;
import com.kryptokrauts.aeternity.generated.model.ChannelSettleTx;
import com.kryptokrauts.aeternity.generated.model.ChannelSlashTx;
import com.kryptokrauts.aeternity.generated.model.ChannelSnapshotSoloTx;
import com.kryptokrauts.aeternity.generated.model.ChannelWithdrawTx;
import com.kryptokrauts.aeternity.generated.model.ContractCallTx;
import com.kryptokrauts.aeternity.generated.model.ContractCreateTx;
import com.kryptokrauts.aeternity.generated.model.GenericTx;
import com.kryptokrauts.aeternity.generated.model.NameClaimTx;
import com.kryptokrauts.aeternity.generated.model.NamePreclaimTx;
import com.kryptokrauts.aeternity.generated.model.NameRevokeTx;
import com.kryptokrauts.aeternity.generated.model.NameUpdateTx;
import com.kryptokrauts.aeternity.generated.model.OracleExtendTx;
import com.kryptokrauts.aeternity.generated.model.OracleQueryTx;
import com.kryptokrauts.aeternity.generated.model.OracleRegisterTx;
import com.kryptokrauts.aeternity.generated.model.OracleRespondTx;
import com.kryptokrauts.aeternity.generated.model.SpendTx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.AbstractTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ChannelCloseMutualTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ChannelCloseSoloTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ChannelCreateTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ChannelDepositTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ChannelSettleTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ChannelSlashTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ChannelSnapshotSoloTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ChannelWithdrawTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ContractCallTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.ContractCreateTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.NameClaimTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.NamePreclaimTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.NameRevokeTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.NameUpdateTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.OracleExtendTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.OracleQueryTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.OracleRegisterTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.OracleRespondTransactionModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.SpendTransactionModel;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ApiModelMapper {

  public static AbstractTransactionModel<?> mapToTransactionModel(GenericTx tx) {
    Function<GenericTx, ? extends AbstractTransactionModel<?>> mappedSupplier =
        txMapping.get(tx.getClass());
    if (mappedSupplier != null) {
      return mappedSupplier.apply(tx);
    }
    return null;
  }

  /**
   * this static map and initializer holds the mapping from generated tx type (API) to aepp-java-sdk
   * transaction models
   */
  private static Map<Class<?>, Function<GenericTx, ? extends AbstractTransactionModel<?>>>
      txMapping = new HashMap<>();

  static {
    txMapping.put(
        ChannelCloseMutualTx.class,
        ChannelCloseMutualTransactionModel.builder().build().getApiToModelFunction());
    txMapping.put(
        ChannelCloseSoloTx.class,
        ChannelCloseSoloTransactionModel.builder().build().getApiToModelFunction());
    txMapping.put(
        ChannelCreateTx.class,
        ChannelCreateTransactionModel.builder().build().getApiToModelFunction());
    txMapping.put(
        ChannelDepositTx.class,
        ChannelDepositTransactionModel.builder().build().getApiToModelFunction());
    txMapping.put(
        ChannelSettleTx.class,
        ChannelSettleTransactionModel.builder().build().getApiToModelFunction());
    txMapping.put(
        ChannelSlashTx.class,
        ChannelSlashTransactionModel.builder().build().getApiToModelFunction());
    txMapping.put(
        ChannelSnapshotSoloTx.class,
        ChannelSnapshotSoloTransactionModel.builder().build().getApiToModelFunction());
    txMapping.put(
        ChannelWithdrawTx.class,
        ChannelWithdrawTransactionModel.builder().build().getApiToModelFunction());

    txMapping.put(
        ContractCallTx.class,
        ContractCallTransactionModel.builder().build().getApiToModelFunction());
    txMapping.put(
        ContractCreateTx.class,
        ContractCreateTransactionModel.builder().build().getApiToModelFunction());

    txMapping.put(
        NameClaimTx.class, NameClaimTransactionModel.builder().build().getApiToModelFunction());
    txMapping.put(
        NamePreclaimTx.class,
        NamePreclaimTransactionModel.builder().build().getApiToModelFunction());
    txMapping.put(
        NameRevokeTx.class, NameRevokeTransactionModel.builder().build().getApiToModelFunction());
    txMapping.put(
        NameUpdateTx.class, NameUpdateTransactionModel.builder().build().getApiToModelFunction());

    txMapping.put(
        OracleExtendTx.class,
        OracleExtendTransactionModel.builder().build().getApiToModelFunction());
    txMapping.put(
        OracleQueryTx.class, OracleQueryTransactionModel.builder().build().getApiToModelFunction());
    txMapping.put(
        OracleRegisterTx.class,
        OracleRegisterTransactionModel.builder().build().getApiToModelFunction());
    txMapping.put(
        OracleRespondTx.class,
        OracleRespondTransactionModel.builder().build().getApiToModelFunction());

    txMapping.put(SpendTx.class, SpendTransactionModel.builder().build().getApiToModelFunction());
  }
}
