package com.kryptokrauts.aeternity.sdk.service.compiler;

import com.kryptokrauts.sophia.compiler.generated.model.ACI;
import com.kryptokrauts.sophia.compiler.generated.model.ByteCode;
import com.kryptokrauts.sophia.compiler.generated.model.Calldata;
import com.kryptokrauts.sophia.compiler.generated.model.SophiaJsonData;
import io.reactivex.Single;
import java.util.List;

public interface CompilerService {

  /**
   * gets the encoded calldata for this contractCode
   *
   * @param contractCode the sourcecode of the contract
   * @return encoded calldata
   */
  Single<Calldata> encodeCalldata(String contractCode, String function, List<String> arguments);

  /**
   * gets the contract bytecode for this contractCode
   *
   * @param contractCode the sourcecode of the contract
   * @return byteCode of the compiled contract
   */
  Single<ByteCode> compile(String contractCode, String srcFile, Object fileSystem);

  /**
   * decodes a calldata
   *
   * @param calldata the calldata
   * @param sophiaType the awaited sophia type
   * @return decoded answer
   */
  Single<SophiaJsonData> decodeCalldata(String calldata, String sophiaType);

  /**
   * generates the ACI for this contractCode
   * https://github.com/aeternity/aesophia/blob/master/docs/aeso_aci.md
   *
   * @param contractCode the sourcecode of the contract
   * @return the ACI for a given contract
   */
  Single<ACI> generateACI(String contractCode);
}
