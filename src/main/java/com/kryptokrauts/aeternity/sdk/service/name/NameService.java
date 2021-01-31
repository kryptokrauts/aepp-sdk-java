package com.kryptokrauts.aeternity.sdk.service.name;

import com.kryptokrauts.aeternity.sdk.service.info.domain.KeyBlockResult;
import com.kryptokrauts.aeternity.sdk.service.name.domain.NameEntryResult;
import io.reactivex.Single;

public interface NameService {

  /**
   * asynchronously returns the nameid object for given aens name
   *
   * @param name the AENS name
   * @return asynchronous result handler (RxJava Single) for {@link KeyBlockResult}
   */
  Single<NameEntryResult> asyncGetNameId(String name);

  /**
   * synchronously returns the nameid object for given aens name
   *
   * @param name the AENS name
   * @return result of {@link NameEntryResult}
   */
  NameEntryResult blockingGetNameId(String name);
}
