package com.fss.aeps.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class EnumMapper<E> {

	private final Map<String, E> enummap;
	private final E defaultValue;

	public EnumMapper(Map<String, String> map, Function<String, E> stringToEnum, E defaultValue) {
		this.enummap = new HashMap<>();
		this.defaultValue = defaultValue;
		map.forEach((k,v) ->{
			enummap.put(k, stringToEnum.apply(v));
		});
	}

	public final E map(final String key) {
		final E val = enummap.get(key);
		return val == null ? defaultValue : val;
	}
}
