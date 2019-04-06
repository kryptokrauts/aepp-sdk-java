package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import java.math.BigInteger;
import com.kryptokrauts.aeternity.generated.api.rxjava.ContractApi;
import com.kryptokrauts.aeternity.generated.model.ContractCreateTx;
import com.kryptokrauts.aeternity.generated.model.CreateContractUnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import io.reactivex.Single;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import net.consensys.cava.bytes.Bytes;
import net.consensys.cava.rlp.RLP;

@Getter
@SuperBuilder
public class CreateContractTransaction extends AbstractTransaction<ContractCreateTx> {

  @NonNull private Integer abiVersion;
  @NonNull private Integer amount;
  @NonNull private String callData;
  @NonNull private String contractByteCode;
  @NonNull private Integer deposit;
  @NonNull private Integer gas;
  @NonNull private Integer gasPrice;
  @NonNull private Integer nonce;
  @NonNull private String ownerId;
  @NonNull private Integer ttl;
  @NonNull private Integer vmVersion;

  @NonNull private ContractApi contractApi;

  @Override
  protected Single<CreateContractUnsignedTx> createInternal() {
    return contractApi.rxPostContractCreate(toModel());
  }

  @Override
  protected ContractCreateTx toModel() {
    ContractCreateTx contractCreateTx = new ContractCreateTx();
    contractCreateTx.setAbiVersion(abiVersion);
    contractCreateTx.setAmount(amount);
    contractCreateTx.setCallData(callData);
    contractCreateTx.setCode(contractByteCode);
    contractCreateTx.setDeposit(deposit);
    contractCreateTx.setFee(fee.intValue());
    contractCreateTx.setGas(gas);
    contractCreateTx.setGasPrice(gasPrice);
    contractCreateTx.setNonce(nonce);
    contractCreateTx.setOwnerId(ownerId);
    contractCreateTx.setTtl(ttl);
    contractCreateTx.setVmVersion(vmVersion);

    return contractCreateTx;
  }

  @Override
  protected Bytes createRLPEncodedList() {
    Bytes encodedRlp =
        RLP.encodeList(
            rlpWriter -> {
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_CONTRACT_CREATE_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN);
              byte[] ownerWithTag =
                  EncodingUtils.decodeCheckAndTag(this.ownerId, SerializationTags.ID_TAG_ACCOUNT);
              rlpWriter.writeByteArray(ownerWithTag);
              rlpWriter.writeInt(this.nonce);
              rlpWriter.writeByteArray(contractByteCode.getBytes());
              rlpWriter.writeInt(3);
              rlpWriter.writeBigInteger(new BigInteger("206170000000000"));
              rlpWriter.writeInt(this.ttl);
              rlpWriter.writeInt(deposit);
              rlpWriter.writeInt(amount);
              rlpWriter.writeInt(gas);
              rlpWriter.writeInt(gasPrice);
              rlpWriter.writeByteArray(callData.getBytes());
            });
    return encodedRlp;
  }
}
