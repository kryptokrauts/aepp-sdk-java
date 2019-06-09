package com.kryptokrauts.aeternity.sdk.service.compiler;

import com.kryptokrauts.sophia.compiler.generated.model.ByteCode;
import com.kryptokrauts.sophia.compiler.generated.model.Calldata;
import io.reactivex.Single;
import java.util.List;

public interface CompilerService {

  /**
   * gets the encoded calldata for this contractCode
   *
   * @param contractCode
   * @return
   */
  Single<Calldata> encodeCalldata(String sourceCode, String function, List<String> arguments);

  /**
   * gets the contract bytecode for this contractCode
   *
   * @param contractCode
   * @return
   */
  Single<ByteCode> compile(String contractCode, String srcFile, Object fileSystem);
}
