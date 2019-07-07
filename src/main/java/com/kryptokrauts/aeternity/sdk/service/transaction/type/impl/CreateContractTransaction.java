package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ContractApi;
import com.kryptokrauts.aeternity.generated.model.ContractCreateTx;
import com.kryptokrauts.aeternity.generated.model.CreateContractUnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import io.reactivex.Single;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;

@Getter
@SuperBuilder
@ToString
public class CreateContractTransaction extends AbstractTransaction<ContractCreateTx> {

  @NonNull private BigInteger abiVersion;
  @NonNull private BigInteger amount;
  @NonNull private String callData;
  @NonNull private String contractByteCode;
  @NonNull private BigInteger deposit;
  @NonNull private BigInteger gas;
  @NonNull private BigInteger gasPrice;
  @NonNull private BigInteger nonce;
  @NonNull private String ownerId;
  @NonNull private BigInteger ttl;
  @NonNull private BigInteger vmVersion;

  @NonNull private ContractApi contractApi;

  @NonNull private DefaultApi compilerApi;

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
    contractCreateTx.setFee(fee);
    contractCreateTx.setGas(gas);
    contractCreateTx.setGasPrice(gasPrice);
    contractCreateTx.setNonce(nonce);
    contractCreateTx.setOwnerId(ownerId);
    contractCreateTx.setTtl(ttl);
    contractCreateTx.setVmVersion(vmVersion);

    return contractCreateTx;
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
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_CONTRACT_CREATE_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN);
              byte[] ownerWithTag =
                  EncodingUtils.decodeCheckAndTag(this.ownerId, SerializationTags.ID_TAG_ACCOUNT);
              rlpWriter.writeByteArray(ownerWithTag);
              this.checkZeroAndWriteValue(rlpWriter, this.nonce);
              rlpWriter.writeByteArray(
                  EncodingUtils.decodeCheckWithIdentifier(this.contractByteCode));
              this.checkZeroAndWriteValue(rlpWriter, this.calculateVersion());
              this.checkZeroAndWriteValue(rlpWriter, this.fee);
              this.checkZeroAndWriteValue(rlpWriter, this.ttl);
              this.checkZeroAndWriteValue(rlpWriter, this.deposit);
              this.checkZeroAndWriteValue(rlpWriter, this.amount);
              this.checkZeroAndWriteValue(rlpWriter, this.gas);
              this.checkZeroAndWriteValue(rlpWriter, this.gasPrice);
              rlpWriter.writeByteArray(EncodingUtils.decodeCheckWithIdentifier(this.callData));
            });
    return encodedRlp;
  }

  private BigInteger calculateVersion() {
    try {
      ByteBuffer vm = ByteBuffer.allocate(8);
      vm.putInt(vmVersion.intValue());
      vm.putInt(abiVersion.intValue());

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      outputStream.write(vm.array());

      StringBuffer versionBuffer = new StringBuffer();

      for (byte b : outputStream.toByteArray()) {
        versionBuffer.append(String.format("%x", b));
      }
      return BigInteger.valueOf(Integer.parseInt(versionBuffer.toString(), 16));
    } catch (Exception e) {
      System.err.println(
          String.format(
              "Error occured calculating version from parameters vmVersion %s and abiVersion %s",
              vmVersion, abiVersion));
      return null;
    }
  }
}
