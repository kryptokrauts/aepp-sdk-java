package com.kryptokrauts.aeternity.sdk.service.compiler;

import com.kryptokrauts.aeternity.sdk.domain.ObjectResultWrapper;
import com.kryptokrauts.aeternity.sdk.domain.StringResultWrapper;
import com.kryptokrauts.aeternity.sdk.service.compiler.domain.ACIResult;
import io.reactivex.Single;
import java.util.List;
import java.util.Map;

public interface CompilerService {

  /**
   * asynchronously gets the encoded calldata for this contractCode
   *
   * @deprecated use {@link CompilerService#asyncEncodeCalldata(String, String, List, Map)} instead.
   *     this method will be removed in future releases
   * @param contractCode the sourcecode of the contract
   * @param function the name of the function to call
   * @param arguments the params that should be passed to the function
   * @return asynchronous result handler (RxJava Single) for encoded calldata
   */
  @Deprecated
  Single<StringResultWrapper> asyncEncodeCalldata(
      String contractCode, String function, List<String> arguments);

  /**
   * asynchronously gets the encoded calldata for this contractCode
   *
   * @param contractCode the sourcecode of the contract
   * @param function the name of the function to call
   * @param arguments the params that should be passed to the function
   * @param fileSystem map with libraryName and code which is passed to the compiler
   * @return asynchronous result handler (RxJava Single) for encoded calldata
   */
  Single<StringResultWrapper> asyncEncodeCalldata(
      String contractCode, String function, List<String> arguments, Map<String, String> fileSystem);

  /**
   * synchronously gets the encoded calldata for this contractCode
   *
   * @deprecated use {@link CompilerService#blockingEncodeCalldata(String, String, List, Map)}
   *     instead. this method will be removed in future releases
   * @param contractCode the sourcecode of the contract
   * @param function the name of the function to call
   * @param arguments the params that should be passed to the function
   * @return encoded calldata
   */
  @Deprecated
  StringResultWrapper blockingEncodeCalldata(
      String contractCode, String function, List<String> arguments);

  /**
   * synchronously gets the encoded calldata for this contractCode
   *
   * @param contractCode the sourcecode of the contract
   * @param function the name of the function to call
   * @param arguments the params that should be passed to the function
   * @param fileSystem map with libraryName and code which is passed to the compiler
   * @return encoded calldata
   */
  StringResultWrapper blockingEncodeCalldata(
      String contractCode, String function, List<String> arguments, Map<String, String> fileSystem);

  /**
   * asynchronously gets the contract bytecode for this contractCode
   *
   * @param contractCode the sourcecode of the contract
   * @param srcFile untested compileOpts value: set null
   * @param fileSystem map with libraryName and code which is passed to the compiler
   * @return asynchronous result handler (RxJava Single) for byteCode of the compiled contract
   */
  Single<StringResultWrapper> asyncCompile(String contractCode, String srcFile, Object fileSystem);

  /**
   * synchronously gets the contract bytecode for this contractCode
   *
   * @param contractCode the sourcecode of the contract
   * @param srcFile untested compileOpts value: set null
   * @param fileSystem map with libraryName and code which is passed to the compiler
   * @return byteCode of the compiled contract
   */
  StringResultWrapper blockingCompile(String contractCode, String srcFile, Object fileSystem);

  /**
   * asynchronously decodes a calldata
   *
   * @param calldata the calldata
   * @param sophiaType the awaited sophia type
   * @return asynchronous result handler (RxJava Single) for decoded answer as json string
   */
  Single<ObjectResultWrapper> asyncDecodeCalldata(String calldata, String sophiaType);

  /**
   * synchronously decodes a calldata
   *
   * @param calldata the calldata
   * @param sophiaType the awaited sophia type
   * @return decoded answer as json string
   */
  ObjectResultWrapper blockingDecodeCalldata(String calldata, String sophiaType);

  /**
   * asynchronously decodes callresult of contract-calls
   *
   * @deprecated use {@link CompilerService#asyncDecodeCallResult(String, String, String, String,
   *     Map)} instead. this method will be removed in future releases
   * @param source the contract source
   * @param function the called function
   * @param callResult the received resultType (ok | error | revert)
   * @param callValue the received value
   * @return the decoded sophia call result
   */
  @Deprecated
  Single<ObjectResultWrapper> asyncDecodeCallResult(
      String source, String function, String callResult, String callValue);

  /**
   * asynchronously decodes callresult of contract-calls
   *
   * @param source the contract source
   * @param function the called function
   * @param callResult the received resultType (ok | error | revert)
   * @param callValue the received value
   * @param fileSystem map with libraryName and code which is passed to the compiler
   * @return the decoded sophia call result
   */
  Single<ObjectResultWrapper> asyncDecodeCallResult(
      String source,
      String function,
      String callResult,
      String callValue,
      Map<String, String> fileSystem);

  /**
   * synchronously decodes callresult of contract-calls
   *
   * @deprecated use {@link CompilerService#blockingDecodeCallResult(String, String, String, String,
   *     Map)} instead. this method will be removed in future releases
   * @param source the contract source
   * @param function the called function
   * @param callResult the received resultType (ok | error | revert)
   * @param callValue the received value
   * @return the decoded sophia call result
   */
  @Deprecated
  ObjectResultWrapper blockingDecodeCallResult(
      String source, String function, String callResult, String callValue);

  /**
   * synchronously decodes callresult of contract-calls
   *
   * @param source the contract source
   * @param function the called function
   * @param callResult the received resultType (ok | error | revert)
   * @param callValue the received value
   * @param fileSystem map with libraryName and code which is passed to the compiler
   * @return the decoded sophia call result
   */
  ObjectResultWrapper blockingDecodeCallResult(
      String source,
      String function,
      String callResult,
      String callValue,
      Map<String, String> fileSystem);

  /**
   * asynchronously generates the ACI for this contractCode
   * https://github.com/aeternity/aesophia/blob/master/docs/aeso_aci.md
   *
   * @param contractCode the sourcecode of the contract
   * @param srcFile untested compileOpts value: set null
   * @param fileSystem map with libraryName and code which is passed to the compiler
   * @return asynchronous result handler (RxJava Single) for {@link ACIResult}
   */
  Single<ACIResult> asyncGenerateACI(String contractCode, String srcFile, Object fileSystem);

  /**
   * synchronously generates the ACI for this contractCode
   * https://github.com/aeternity/aesophia/blob/master/docs/aeso_aci.md
   *
   * @param contractCode the sourcecode of the contract
   * @param srcFile untested compileOpts value: set null
   * @param fileSystem map with libraryName and code which is passed to the compiler
   * @return result of {@link ACIResult}
   */
  ACIResult blockingGenerateACI(String contractCode, String srcFile, Object fileSystem);
}
