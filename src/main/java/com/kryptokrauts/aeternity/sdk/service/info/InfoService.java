package com.kryptokrauts.aeternity.sdk.service.info;

import com.kryptokrauts.aeternity.generated.model.GenericSignedTx;
import com.kryptokrauts.aeternity.generated.model.GenericTxs;
import com.kryptokrauts.aeternity.generated.model.KeyBlock;
import com.kryptokrauts.aeternity.generated.model.TxInfoObject;
import io.reactivex.Single;

public interface InfoService {

  /**
   * get the transaction object for given tx hash
   *
   * @param txHash
   * @return
   */
  Single<GenericSignedTx> getTransactionByHash(String txHash);

  /**
   * gets the information object for given tx hash
   *
   * @param txHash
   * @return
   */
  Single<TxInfoObject> getTransactionInfoByHash(String txHash);

  /**
   * get transaction object for given microblock tx hash
   *
   * @param microBlockHash
   * @return
   */
  Single<GenericTxs> getMicroBlockTransactions(String microBlockHash);

  Single<KeyBlock> asyncGetCurrentKeyBlock();

  KeyBlock blockingGetCurrentKeyBlock();
}
