package com.kryptokrauts.aeternity.sdk.service.aens;

import com.kryptokrauts.aeternity.sdk.service.info.domain.KeyBlockResult;
import com.kryptokrauts.aeternity.sdk.service.name.domain.NameIdResult;
import io.reactivex.Single;

public interface NameService {

  /**
   * asynchronously returns the nameid object for given aens name
   *
   * @param name
   * @return asynchronous result handler (RxJava Single) for {@link KeyBlockResult}
   */
  public Single<NameIdResult> asyncGetNameId(String name);

  /**
   * synchronously returns the nameid object for given aens name
   *
   * @param name
   * @return result of {@link NameIdResult}
   */
  public NameIdResult blockingGetNameId(String name);
}
