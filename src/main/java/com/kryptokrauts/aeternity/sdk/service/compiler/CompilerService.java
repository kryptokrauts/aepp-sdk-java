package com.kryptokrauts.aeternity.sdk.service.compiler;

import com.kryptokrauts.aeternity.sdk.service.compiler.domain.ACIResult;
import io.reactivex.Single;
import java.util.List;

public interface CompilerService {

  /**
   * gets the encoded calldata for this contractCode
   *
   * @param contractCode the sourcecode of the contract
   * @return async encoded calldata
   */
  Single<String> asyncEncodeCalldata(String contractCode, String function, List<String> arguments);

  /**
   * gets the encoded calldata for this contractCode
   *
   * @param contractCode the sourcecode of the contract
   * @return encoded calldata
   */
  String blockingEncodeCalldata(String contractCode, String function, List<String> arguments);

  /**
   * gets the contract bytecode for this contractCode
   *
   * @param contractCode the sourcecode of the contract
   * @return async byteCode of the compiled contract
   */
  Single<String> asyncCompile(String contractCode, String srcFile, Object fileSystem);

  /**
   * gets the contract bytecode for this contractCode
   *
   * @param contractCode the sourcecode of the contract
   * @return byteCode of the compiled contract
   */
  String blockingCompile(String contractCode, String srcFile, Object fileSystem);

  /**
   * decodes a calldata
   *
   * @param calldata the calldata
   * @param sophiaType the awaited sophia type
   * @return async decoded answer as json string
   */
  Single<Object> asyncDecodeCalldata(String calldata, String sophiaType);

  /**
   * decodes a calldata
   *
   * @param calldata the calldata
   * @param sophiaType the awaited sophia type
   * @return decoded answer as json string
   */
  Object blockingDecodeCalldata(String calldata, String sophiaType);

  /**
   * generates the ACI for this contractCode
   * https://github.com/aeternity/aesophia/blob/master/docs/aeso_aci.md
   *
   * @param contractCode the sourcecode of the contract
   * @return asnyc ACI for a given contract
   */
  Single<ACIResult> asyncGenerateACI(String contractCode, String srcFile, Object fileSystem);

  /**
   * generates the ACI for this contractCode
   * https://github.com/aeternity/aesophia/blob/master/docs/aeso_aci.md
   *
   * @param contractCode the sourcecode of the contract
   * @return the ACI for a given contract
   */
  ACIResult blockingGenerateACI(String contractCode, String srcFile, Object fileSystem);
}
