package com.kryptokrauts.aeternity.sdk.service.compiler;

import com.kryptokrauts.aeternity.sdk.service.compiler.domain.ACIResult;
import io.reactivex.Single;
import java.util.List;

public interface CompilerService {

  /**
   * asynchronously gets the encoded calldata for this contractCode
   *
   * @param contractCode the sourcecode of the contract
   * @return asynchronous result handler (RxJava Single) for encoded calldata
   */
  Single<String> asyncEncodeCalldata(String contractCode, String function, List<String> arguments);

  /**
   * synchronously gets the encoded calldata for this contractCode
   *
   * @param contractCode the sourcecode of the contract
   * @return encoded calldata
   */
  String blockingEncodeCalldata(String contractCode, String function, List<String> arguments);

  /**
   * asynchronously gets the contract bytecode for this contractCode
   *
   * @param contractCode the sourcecode of the contract
   * @return asynchronous result handler (RxJava Single) for byteCode of the compiled contract
   */
  Single<String> asyncCompile(String contractCode, String srcFile, Object fileSystem);

  /**
   * synchronously gets the contract bytecode for this contractCode
   *
   * @param contractCode the sourcecode of the contract
   * @return byteCode of the compiled contract
   */
  String blockingCompile(String contractCode, String srcFile, Object fileSystem);

  /**
   * asynchronously decodes a calldata
   *
   * @param calldata the calldata
   * @param sophiaType the awaited sophia type
   * @return asynchronous result handler (RxJava Single) for decoded answer as json string
   */
  Single<Object> asyncDecodeCalldata(String calldata, String sophiaType);

  /**
   * synchronously decodes a calldata
   *
   * @param calldata the calldata
   * @param sophiaType the awaited sophia type
   * @return decoded answer as json string
   */
  Object blockingDecodeCalldata(String calldata, String sophiaType);

  /**
   * asynchronously generates the ACI for this contractCode
   * https://github.com/aeternity/aesophia/blob/master/docs/aeso_aci.md
   *
   * @param contractCode the sourcecode of the contract
   * @return asynchronous result handler (RxJava Single) for {@link ACIResult}
   */
  Single<ACIResult> asyncGenerateACI(String contractCode, String srcFile, Object fileSystem);

  /**
   * synchronously generates the ACI for this contractCode
   * https://github.com/aeternity/aesophia/blob/master/docs/aeso_aci.md
   *
   * @param contractCode the sourcecode of the contract
   * @return result of {@linkACIResult}
   */
  ACIResult blockingGenerateACI(String contractCode, String srcFile, Object fileSystem);
}
