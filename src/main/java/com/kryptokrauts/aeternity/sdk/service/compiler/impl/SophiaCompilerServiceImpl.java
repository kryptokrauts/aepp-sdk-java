package com.kryptokrauts.aeternity.sdk.service.compiler.impl;

import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.compiler.CompilerService;
import com.kryptokrauts.aeternity.sdk.service.compiler.domain.ACIResult;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import com.kryptokrauts.sophia.compiler.generated.model.CompileOpts;
import com.kryptokrauts.sophia.compiler.generated.model.Contract;
import com.kryptokrauts.sophia.compiler.generated.model.FunctionCallInput;
import com.kryptokrauts.sophia.compiler.generated.model.SophiaBinaryData;
import com.kryptokrauts.sophia.compiler.generated.model.SophiaJsonData;
import io.netty.util.internal.StringUtil;
import io.reactivex.Single;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class SophiaCompilerServiceImpl implements CompilerService {

  @NonNull private ServiceConfiguration config;

  @NonNull private DefaultApi compilerApi;

  @Override
  public Single<String> asyncEncodeCalldata(
      String sourceCode, String function, List<String> arguments) {
    return this.compilerApi
        .rxEncodeCalldata(buildFunctionCallInput(sourceCode, function, arguments))
        .map(calldata -> calldata.getCalldata());
  }

  @Override
  public String blockingEncodeCalldata(String sourceCode, String function, List<String> arguments) {
    return this.compilerApi
        .rxEncodeCalldata(buildFunctionCallInput(sourceCode, function, arguments))
        .blockingGet()
        .getCalldata();
  }

  private FunctionCallInput buildFunctionCallInput(
      String sourceCode, String function, List<String> arguments) {
    FunctionCallInput body = new FunctionCallInput();
    body.source(sourceCode);
    body.function(function);
    if (arguments != null) {
      for (String argument : arguments) {
        body.addArgumentsItem(argument);
      }
    }
    body.options(new CompileOpts().backend(config.getTargetVM().getBackendEnum()));
    return body;
  }

  @Override
  public Single<Object> asyncDecodeCalldata(String calldata, String sophiaType) {
    return Optional.ofNullable(this.compilerApi.rxDecodeData(buildDecodeBody(calldata, sophiaType)))
        .orElse(Single.just(new SophiaJsonData()))
        .map(s -> s.getData());
  }

  @Override
  public Object blockingDecodeCalldata(String calldata, String sophiaType) {
    return Optional.ofNullable(
            this.compilerApi.rxDecodeData(buildDecodeBody(calldata, sophiaType)).blockingGet())
        .orElse(new SophiaJsonData())
        .getData();
  }

  private SophiaBinaryData buildDecodeBody(String calldata, String sophiaType) {
    SophiaBinaryData body = new SophiaBinaryData();
    body.setData(calldata);
    body.setSophiaType(sophiaType);
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
  public Single<String> asyncCompile(String contractCode, String srcFile, Object fileSystem) {
    return this.compilerApi
        .rxCompileContract(buildContractBody(contractCode, srcFile, fileSystem))
        .map(byteCode -> byteCode.getBytecode());
  }

  @Override
  public String blockingCompile(String contractCode, String srcFile, Object fileSystem) {
    return this.compilerApi
        .rxCompileContract(buildContractBody(contractCode, srcFile, fileSystem))
        .blockingGet()
        .getBytecode();
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
