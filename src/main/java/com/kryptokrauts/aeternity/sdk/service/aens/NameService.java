package com.kryptokrauts.aeternity.sdk.service.aens;

import com.kryptokrauts.aeternity.sdk.service.name.domain.NameIdResult;
import io.reactivex.Single;

public interface NameService {

  /**
   * asynchronously returns the nameid object for given aens name
   *
   * @param name
   * @return
   */
  public Single<NameIdResult> asyncGetNameId(String name);

  /**
   * synchronously returns the nameid object for given aens name
   *
   * @param name
   * @return
   */
  public NameIdResult blockingGetNameId(String name);
}
