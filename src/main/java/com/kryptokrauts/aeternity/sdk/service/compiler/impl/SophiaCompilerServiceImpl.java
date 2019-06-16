package com.kryptokrauts.aeternity.sdk.service.compiler.impl;

import java.util.List;

import javax.annotation.Nonnull;

import com.kryptokrauts.aeternity.sdk.service.ServiceConfiguration;
import com.kryptokrauts.aeternity.sdk.service.compiler.CompilerService;
import com.kryptokrauts.sophia.compiler.generated.api.DefaultApiImpl;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import com.kryptokrauts.sophia.compiler.generated.model.ByteCode;
import com.kryptokrauts.sophia.compiler.generated.model.Calldata;
import com.kryptokrauts.sophia.compiler.generated.model.CompileOpts;
import com.kryptokrauts.sophia.compiler.generated.model.Contract;
import com.kryptokrauts.sophia.compiler.generated.model.FunctionCallInput;
import com.kryptokrauts.sophia.compiler.generated.model.SophiaBinaryData;
import com.kryptokrauts.sophia.compiler.generated.model.SophiaJsonData;

import io.netty.util.internal.StringUtil;
import io.reactivex.Single;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class SophiaCompilerServiceImpl implements CompilerService {

	@Nonnull
	private ServiceConfiguration config;

	private DefaultApi compilerApi;

	private DefaultApi getCompilerApi() {
		if (compilerApi == null) {
			compilerApi = new DefaultApi(new DefaultApiImpl(config.getCompilerApiClient()));
		}
		return compilerApi;
	}

	@Override
	public Single<Calldata> encodeCalldata(String sourceCode, String function, List<String> arguments) {
		FunctionCallInput body = new FunctionCallInput();
		body.source(sourceCode);
		body.function(function);
		if (arguments != null) {
			for (String argument : arguments) {
				body.addArgumentsItem(argument);
			}
		}
		return this.getCompilerApi().rxEncodeCalldata(body);
	}

	@Override
	public Single<SophiaJsonData> decodeCalldata(String calldata, String sophiaType) {
		SophiaBinaryData body = new SophiaBinaryData();
		body.setData(calldata);
		body.setSophiaType(sophiaType);
		return this.getCompilerApi().rxDecodeData(body);
	}

	@Override
	public Single<ByteCode> compile(String contractCode, String srcFile, Object fileSystem) {
		Contract body = new Contract().code(contractCode);
		CompileOpts compileOpts = new CompileOpts();
		if (!StringUtil.isNullOrEmpty(srcFile)) {
			compileOpts.setSrcFile(srcFile);
		}
		if (fileSystem != null) {
			compileOpts.setFileSystem(fileSystem);
		}
		body.setOptions(compileOpts);
		return this.getCompilerApi().rxCompileContract(body);
	}
}
