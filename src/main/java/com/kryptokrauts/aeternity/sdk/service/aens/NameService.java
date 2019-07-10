package com.kryptokrauts.aeternity.sdk.service.aens;

import com.kryptokrauts.aeternity.generated.model.NameEntry;
import io.reactivex.Single;

public interface NameService {

  Single<NameEntry> getNameId(String name);
}
