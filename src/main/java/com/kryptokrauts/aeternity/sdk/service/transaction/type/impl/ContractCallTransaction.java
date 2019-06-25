package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ContractApi;
import com.kryptokrauts.aeternity.generated.model.ContractCallTx;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import io.reactivex.Single;
import java.math.BigInteger;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;

@Getter
@SuperBuilder
@ToString
public class ContractCallTransaction extends AbstractTransaction<ContractCallTx> {

  @NonNull private BigInteger abiVersion;
  @NonNull private BigInteger amount;
  @NonNull private String callData;
  @NonNull private String callerId;
  @NonNull private String contractId;
  @NonNull private BigInteger gas;
  @NonNull private BigInteger gasPrice;
  @NonNull private BigInteger nonce;

  @NonNull private BigInteger ttl;

  @NonNull private ContractApi contractApi;

  @Override
  protected Single<UnsignedTx> createInternal() {
    return contractApi.rxPostContractCall(getApiModel());
  }

  @Override
  protected ContractCallTx toModel() {
    ContractCallTx contractCallTx = new ContractCallTx();
    contractCallTx.setAbiVersion(abiVersion);
    contractCallTx.setAmount(amount);
    contractCallTx.setCallData(callData);
    contractCallTx.setCallerId(callerId);
    contractCallTx.setContractId(contractId);
    contractCallTx.setFee(fee);
    contractCallTx.setGas(gas);
    contractCallTx.setGasPrice(gasPrice);
    contractCallTx.setNonce(nonce);
    contractCallTx.setTtl(ttl);

    return contractCallTx;
  }

  @Override
  protected void validateInput() {
    // nothing to validate here
  }

  @Override
  protected Bytes createRLPEncodedList() {
    Bytes encodedRlp =
        RLP.encodeList(
            rlpWriter -> {
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_CONTRACT_CALL_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN);
              byte[] callerWithTag =
                  EncodingUtils.decodeCheckAndTag(this.callerId, SerializationTags.ID_TAG_ACCOUNT);
              rlpWriter.writeByteArray(callerWithTag);
              this.checkZeroAndWriteValue(rlpWriter, this.nonce);
              byte[] contractWithTag =
                  EncodingUtils.decodeCheckAndTag(
                      this.contractId, SerializationTags.ID_TAG_CONTRACT);
              rlpWriter.writeByteArray(contractWithTag);
              this.checkZeroAndWriteValue(rlpWriter, this.abiVersion);
              this.checkZeroAndWriteValue(rlpWriter, this.fee);
              this.checkZeroAndWriteValue(rlpWriter, this.ttl);
              this.checkZeroAndWriteValue(rlpWriter, this.amount);
              this.checkZeroAndWriteValue(rlpWriter, this.gas);
              this.checkZeroAndWriteValue(rlpWriter, this.gasPrice);
              rlpWriter.writeByteArray(EncodingUtils.decodeCheckWithIdentifier(this.callData));
            });
    return encodedRlp;
  }

  /**
   * this method can be used to set a an optional amount which is sent to the contract (refunded if
   * the execution fails)
   *
   * @param optional amount
   */
  public void setAmount(BigInteger amount) {
    this.amount = amount;
  }
}
