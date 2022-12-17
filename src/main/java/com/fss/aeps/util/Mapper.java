package com.fss.aeps.util;

import java.util.HashMap;
import java.util.Map;

public final class Mapper {

	private final Map<String, String> map = new HashMap<>();

	public Mapper(Map<String, String> map) {
		this.map.putAll(map);
	}

	public final String map(final String key) {
		final String val = map.get(key);
		return val == null ? key : val;
	}

	public final String mapOrDefault(final String key, final String defVal) {
		final String val = map.get(key);
		return val == null ? defVal : val;
	}

	public boolean reInitialize(final Map<String, String> map) {
		if(map == null || map.isEmpty()) return false;
		this.map.clear();
		this.map.putAll(map);
		return true;
	}
}
