package com.kryptokrauts.aeternity.sdk.service.compiler.impl;

import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.compiler.CompilerService;
import com.kryptokrauts.sophia.compiler.generated.api.DefaultApiImpl;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import com.kryptokrauts.sophia.compiler.generated.model.ACI;
import com.kryptokrauts.sophia.compiler.generated.model.CompileOpts;
import com.kryptokrauts.sophia.compiler.generated.model.Contract;
import com.kryptokrauts.sophia.compiler.generated.model.FunctionCallInput;
import com.kryptokrauts.sophia.compiler.generated.model.SophiaBinaryData;
import com.kryptokrauts.sophia.compiler.generated.model.SophiaJsonData;
import io.netty.util.internal.StringUtil;
import io.reactivex.Single;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class SophiaCompilerServiceImpl implements CompilerService {

  @Nonnull private ServiceConfiguration config;

  private DefaultApi compilerApi;

  private DefaultApi getCompilerApi() {
    if (compilerApi == null) {
      compilerApi = new DefaultApi(new DefaultApiImpl(config.getCompilerApiClient()));
    }
    return compilerApi;
  }

  @Override
  public Single<String> asyncEncodeCalldata(
      String sourceCode, String function, List<String> arguments) {
    return this.getCompilerApi()
        .rxEncodeCalldata(buildFunctionCallInput(sourceCode, function, arguments))
        .map(calldata -> calldata.getCalldata());
  }

  @Override
  public String blockingEncodeCalldata(String sourceCode, String function, List<String> arguments) {
    return this.getCompilerApi()
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
    return body;
  }

  @Override
  public Single<Object> asyncDecodeCalldata(String calldata, String sophiaType) {
    return Optional.ofNullable(
            this.getCompilerApi().rxDecodeData(buildDecodeBody(calldata, sophiaType)))
        .orElse(Single.just(new SophiaJsonData()))
        .map(s -> s.getData());
  }

  @Override
  public Object blockingDecodeCalldata(String calldata, String sophiaType) {
    return Optional.ofNullable(
            this.getCompilerApi().rxDecodeData(buildDecodeBody(calldata, sophiaType)).blockingGet())
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
  public Single<ACI> asyncGenerateACI(String contractCode) {
    return this.getCompilerApi().rxGenerateACI(buildACIBody(contractCode));
  }

  @Override
  public ACI blockingGenerateACI(String contractCode) {
    return this.getCompilerApi().rxGenerateACI(buildACIBody(contractCode)).blockingGet();
  }

  private Contract buildACIBody(String contractCode) {
    Contract body = new Contract().code(contractCode);
    CompileOpts compileOpts = new CompileOpts();
    body.setOptions(compileOpts);
    return body;
  }

  @Override
  public Single<String> asyncCompile(String contractCode, String srcFile, Object fileSystem) {
    return this.getCompilerApi()
        .rxCompileContract(buildContractBody(contractCode, srcFile, fileSystem))
        .map(byteCode -> byteCode.getBytecode());
  }

  @Override
  public String blockingCompile(String contractCode, String srcFile, Object fileSystem) {
    return this.getCompilerApi()
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
    body.setOptions(compileOpts);
    return body;
  }
}
