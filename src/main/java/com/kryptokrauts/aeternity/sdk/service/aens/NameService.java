package com.kryptokrauts.aeternity.sdk.service.aens;

import com.kryptokrauts.aeternity.sdk.service.domain.name.NameIdResult;

import io.reactivex.Single;

public interface NameService {

	Single<NameIdResult> asyncGetNameId(String name);

	NameIdResult blockingGetNameId(String name);
}
