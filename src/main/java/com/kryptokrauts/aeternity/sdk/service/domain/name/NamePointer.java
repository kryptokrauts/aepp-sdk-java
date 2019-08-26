package com.kryptokrauts.aeternity.sdk.service.domain.name;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NamePointer {
	private String key;

	private String id;
}
