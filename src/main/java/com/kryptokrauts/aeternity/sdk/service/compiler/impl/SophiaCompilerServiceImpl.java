package com.kryptokrauts.aeternity.sdk.service.compiler.impl;

import com.kryptokrauts.aeternity.sdk.domain.ObjectResultWrapper;
import com.kryptokrauts.aeternity.sdk.domain.StringResultWrapper;
import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.compiler.CompilerService;
import com.kryptokrauts.aeternity.sdk.service.compiler.domain.ACIResult;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import com.kryptokrauts.sophia.compiler.generated.model.CompileOpts;
import com.kryptokrauts.sophia.compiler.generated.model.Contract;
import com.kryptokrauts.sophia.compiler.generated.model.FunctionCallInput;
import com.kryptokrauts.sophia.compiler.generated.model.SophiaBinaryData;
import com.kryptokrauts.sophia.compiler.generated.model.SophiaCallResultInput;
import com.kryptokrauts.sophia.compiler.generated.model.SophiaJsonData;
import io.netty.util.internal.StringUtil;
import io.reactivex.Single;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class SophiaCompilerServiceImpl implements CompilerService {

  @NonNull private ServiceConfiguration config;

  @NonNull private DefaultApi compilerApi;

  @Override
  public Single<StringResultWrapper> asyncEncodeCalldata(
      String sourceCode, String function, List<String> arguments, Map<String, String> fileSystem) {
    return StringResultWrapper.builder()
        .build()
        .asyncGet(
            this.compilerApi
                .rxEncodeCalldata(
                    buildFunctionCallInput(sourceCode, function, arguments, fileSystem))
                .map(calldata -> calldata.getCalldata()));
  }

  @Override
  public StringResultWrapper blockingEncodeCalldata(
      String sourceCode, String function, List<String> arguments, Map<String, String> fileSystem) {
    return StringResultWrapper.builder()
        .build()
        .blockingGet(
            this.compilerApi
                .rxEncodeCalldata(
                    buildFunctionCallInput(sourceCode, function, arguments, fileSystem))
                .map(calldata -> calldata.getCalldata()));
  }

  private FunctionCallInput buildFunctionCallInput(
      String sourceCode, String function, List<String> arguments, Map<String, String> fileSystem) {
    FunctionCallInput body = new FunctionCallInput();
    body.source(sourceCode);
    body.function(function);
    if (arguments != null) {
      for (String argument : arguments) {
        body.addArgumentsItem(argument);
      }
    }
    body.options(
        new CompileOpts().fileSystem(fileSystem).backend(config.getTargetVM().getBackendEnum()));
    return body;
  }

  @Override
  public Single<ObjectResultWrapper> asyncDecodeCalldata(String calldata, String sophiaType) {
    return ObjectResultWrapper.builder()
        .build()
        .asyncGet(
            this.compilerApi
                .rxDecodeData(buildDecodeBody(calldata, sophiaType))
                .map(
                    decodeResult ->
                        Optional.ofNullable(decodeResult).orElse(new SophiaJsonData()).getData()));
  }

  @Override
  public ObjectResultWrapper blockingDecodeCalldata(String calldata, String sophiaType) {
    return ObjectResultWrapper.builder()
        .build()
        .blockingGet(
            this.compilerApi
                .rxDecodeData(buildDecodeBody(calldata, sophiaType))
                .map(
                    decodeResult ->
                        Optional.ofNullable(decodeResult).orElse(new SophiaJsonData()).getData()));
  }

  @Override
  public Single<ObjectResultWrapper> asyncDecodeCallResult(
      String source,
      String function,
      String callResult,
      String callValue,
      Map<String, String> fileSystem) {
    return ObjectResultWrapper.builder()
        .build()
        .asyncGet(
            this.compilerApi
                .rxDecodeCallResult(
                    buildDecodeBody(source, function, callResult, callValue, fileSystem))
                .map(decodeResult -> Optional.ofNullable(decodeResult).orElse("")));
  }

  @Override
  public ObjectResultWrapper blockingDecodeCallResult(
      String source,
      String function,
      String callResult,
      String callValue,
      Map<String, String> fileSystem) {
    return ObjectResultWrapper.builder()
        .build()
        .blockingGet(
            this.compilerApi
                .rxDecodeCallResult(
                    buildDecodeBody(source, function, callResult, callValue, fileSystem))
                .map(decodeResult -> Optional.ofNullable(decodeResult).orElse("")));
  }

  private SophiaBinaryData buildDecodeBody(String calldata, String sophiaType) {
    SophiaBinaryData body = new SophiaBinaryData();
    body.setData(calldata);
    body.setSophiaType(sophiaType);
    return body;
  }

  private SophiaCallResultInput buildDecodeBody(
      String source,
      String function,
      String callResult,
      String callValue,
      Map<String, String> fileSystem) {
    SophiaCallResultInput body = new SophiaCallResultInput();
    body.setSource(source);
    body.setFunction(function);
    body.setCallResult(callResult);
    body.setCallValue(callValue);
    CompileOpts compileOpts = new CompileOpts();
    compileOpts.setBackend(config.getTargetVM().getBackendEnum());
    compileOpts.setFileSystem(fileSystem);
    body.setOptions(compileOpts);
    return body;
  }

  @Override
  public Single<ACIResult> asyncGenerateACI(
      String contractCode, String srcFile, Object fileSystem) {
    return ACIResult.builder()
        .build()
        .asyncGet(this.compilerApi.rxGenerateACI(buildACIBody(contractCode, srcFile, fileSystem)));
  }

  @Override
  public ACIResult blockingGenerateACI(String contractCode, String srcFile, Object fileSystem) {
    return ACIResult.builder()
        .build()
        .blockingGet(
            this.compilerApi.rxGenerateACI(buildACIBody(contractCode, srcFile, fileSystem)));
  }

  private Contract buildACIBody(String contractCode, String srcFile, Object fileSystem) {
    Contract body = new Contract().code(contractCode);
    CompileOpts compileOpts = new CompileOpts();
    if (fileSystem != null) {
      compileOpts.fileSystem(fileSystem);
    }
    if (!StringUtil.isNullOrEmpty(srcFile)) {
      compileOpts.setSrcFile(srcFile);
    }
    compileOpts.setBackend(config.getTargetVM().getBackendEnum());
    body.setOptions(compileOpts);
    return body;
  }

  @Override
  public Single<StringResultWrapper> asyncCompile(
      String contractCode, String srcFile, Object fileSystem) {
    return StringResultWrapper.builder()
        .build()
        .asyncGet(
            this.compilerApi
                .rxCompileContract(buildContractBody(contractCode, srcFile, fileSystem))
                .map(byteCode -> byteCode.getBytecode()));
  }

  @Override
  public StringResultWrapper blockingCompile(
      String contractCode, String srcFile, Object fileSystem) {
    return StringResultWrapper.builder()
        .build()
        .blockingGet(
            this.compilerApi
                .rxCompileContract(buildContractBody(contractCode, srcFile, fileSystem))
                .map(bytecode -> bytecode.getBytecode()));
  }

  private Contract buildContractBody(String contractCode, String srcFile, Object fileSystem) {
    Contract body = new Contract().code(contractCode);
    CompileOpts compileOpts = new CompileOpts();
    if (!StringUtil.isNullOrEmpty(srcFile)) {
      compileOpts.setSrcFile(srcFile);
    }
    if (fileSystem != null) {
      compileOpts.setFileSystem(fileSystem);
    }
    compileOpts.setBackend(config.getTargetVM().getBackendEnum());
    body.setOptions(compileOpts);
    return body;
  }
}
