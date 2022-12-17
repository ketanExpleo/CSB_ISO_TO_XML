package com.fss.aeps.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fss.aeps.jaxb.DeviceTagNameType;
import com.fss.aeps.jaxb.DeviceType.Tag;

public final class DeviceTagMap {

private static final Logger logger = LoggerFactory.getLogger(DeviceTagMap.class);

	public static final String toTlvString(final List<Tag> tags) {
		final StringBuilder sb = new StringBuilder();
		for (final Tag tag : tags) {
			final DeviceTagNameType name = tag.getName();
			if(name == null) {
				logger.warn("unknown tag in payer device type. kindly inspect and fix");
				continue;
			}
			final String value = tag.getValue();
			sb.append(String.format("%03d", name.name().length())).append(name.name());
			sb.append(String.format("%03d", value.length())).append(value);
		}
		return sb.toString();
	}


	public static final List<Tag> toTagList(final String tlv) {
		if(tlv == null) return List.of();
		final List<Tag> tags = new ArrayList<>();
		int pointer = 0;
		while(pointer < tlv.length()) {
			final int nlen = Integer.parseInt(tlv.substring(pointer, pointer+3));
			pointer+=3;
			final DeviceTagNameType type = DeviceTagNameType.valueOf(tlv.substring(pointer, pointer+nlen));
			pointer+=nlen;
			final int vlen = Integer.parseInt(tlv.substring(pointer, pointer+3));
			pointer+=3;
			final String value = tlv.substring(pointer, pointer+vlen);
			pointer+=vlen;
			tags.add(new Tag(type, value));
		}
		return tags;
	}

	public static final Map<DeviceTagNameType, String> toMap(final List<Tag> tags) {
		final Map<DeviceTagNameType, String> map = new HashMap<>();
		for (final Tag tag : tags) {
			final DeviceTagNameType name = tag.getName();
			if(name == null) {
				logger.warn("unknown tag in payer device type. kindly inspect and fix");
				continue;
			}
			map.put(name, tag.getValue());
		}
		return map;
	}


	public static final Map<DeviceTagNameType, String> toMap(final String tlv) {
		final Map<DeviceTagNameType, String> map = new HashMap<>();
		if(tlv == null) return Map.of();
		int pointer = 0;
		while(pointer < tlv.length()) {
			final int nlen = Integer.parseInt(tlv.substring(pointer, pointer+3));
			pointer+=3;
			final DeviceTagNameType type = DeviceTagNameType.valueOf(tlv.substring(pointer, pointer+nlen));
			pointer+=nlen;
			final int vlen = Integer.parseInt(tlv.substring(pointer, pointer+3));
			pointer+=3;
			final String value = tlv.substring(pointer, pointer+vlen);
			pointer+=vlen;
			map.put(type, value);
		}
		return map;
	}

}
