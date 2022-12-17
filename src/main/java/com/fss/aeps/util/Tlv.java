package com.fss.aeps.util;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;


public final class Tlv {

	private static final int   size   = 3;
	public static final String format = "%0" + size + "d";

	private static final int   width  = 60;
	private static final char[] separator = "-".repeat(width * 2 + 4).toCharArray();
	private static final String[] spaces = new String[width * 2 - 10];

	private final Map<String, String> tlvmap;

	public Tlv() {
		this.tlvmap = new LinkedHashMap<>();
	}

	public Tlv(final boolean sorted) {
		if(sorted) this.tlvmap = new TreeMap<>();
		else this.tlvmap = new LinkedHashMap<>();
	}


	public final int size() {
		return tlvmap.size();
	}


	public boolean isEmpty() {
		return tlvmap.isEmpty();
	}


	public boolean containsKey(final String key) {
		return tlvmap.containsKey(key);
	}


	public boolean containsValue(final String value) {
		return tlvmap.containsValue(value);
	}


	public String get(final String tag) {
		return tlvmap.get(tag);
	}

	public final String getOrElse(final String tag, final String elseVal) {
		String val = tlvmap.get(tag);
		return val == null ? elseVal : val;
	}


	public final Tlv put(final String tag, final String value) {
		if(tag == null || value == null) throw new RuntimeException("tag '"+tag+"' or value '"+value+"' cannot be null.");
		if(tag.length() != 3) throw new RuntimeException("tag '"+tag+"' must be of length "+size);
		tlvmap.put(tag, value);
		return this;
	}

	public final Tlv putNullable(final String tag, final String value) {
		if(tag == null || value == null || tag.length() != 3) return this;
		tlvmap.put(tag, value);
		return this;
	}

	public final void putAll(final Tlv tlv) {
		tlvmap.putAll(tlv.tlvmap);
	}

	public final String remove(final String tag) {
		return tlvmap.remove(tag);
	}

	public final boolean removeAll(final Collection<String> tags) {
		return tlvmap.keySet().removeAll(tags);
	}

	public final boolean retainAll(final Collection<String> tags) {
		return tlvmap.keySet().retainAll(tags);
	}

	public final void clear() {
		tlvmap.clear();
	}

	public final Set<String> tagSet() {
		return tlvmap.keySet();
	}

	public final Collection<String> values() {
		return tlvmap.values();
	}

	public final Set<Entry<String, String>> entrySet() {
		return tlvmap.entrySet();
	}

	public static final Tlv parse(final String tlvString) {
		final Tlv tlv = new Tlv();
		if (tlvString == null || tlvString.length() == 0) return tlv;
		int i = 0;
		try {
			while (i < tlvString.length()) {
				String tagname = tlvString.substring(i, i + size);
				i = i + size;
				int taglen = Integer.parseInt(tlvString.substring(i, i + size));
				i = i + size;
				tlv.tlvmap.put(tagname, tlvString.substring(i, i + taglen));
				i = i + taglen;
			}
		} catch (Exception e) {
			throw new RuntimeException("parse error at "+i+" for tlv string "+tlvString, e);
		}
		return tlv;
	}

	public final String build() {
		final StringBuilder sb = new StringBuilder(50);
		for (Entry<String, String> entry : tlvmap.entrySet()) {
			String val = entry.getValue();
			sb.append(entry.getKey());
			sb.append(String.format(format, val.length()));
			sb.append(val);
		}
		return sb.toString();
	}

	@Override
	public final Tlv clone() {
		final boolean sorted = tlvmap instanceof TreeMap;
		final Tlv tlv = new Tlv(sorted);
		tlv.tlvmap.putAll(tlvmap);
		return tlv;
	}

	public final StringBuilder print() {
		boolean flip = false;
		final StringBuilder sb = new StringBuilder(30);
		sb.append("\r\n").append(separator).append("\r\n");
		for(Entry<String, String> e : tlvmap.entrySet()) {
			final String key = e.getKey();
			final String val = e.getValue();
			if (val.length() > (width - 10) && flip) sb.append("\r\n");
			else if (val.length() > (width - 10))flip ^= true;
			sb.append("| ").append(key).append(" : '").append(val).append("'").append(spaces[Math.max(width - (8 + val.length()), 0)]);
			if (flip) {
				sb.append("|\r\n");
			} else if (val.length() > (width - 8)) sb.append(spaces[width * 2 - val.length() - 6]).append("|\r\n");
			flip ^= true;

		}
		if (flip) sb.append("\r\n");
		sb.append(separator);
		return sb;
	}

	public static void main(String[] args) {
		Tlv tlv = Tlv
				.parse("001009nnnyFMRnn005001P006006100001008001X009015IPPBCBS00000001010012STARTEK.ACPL011012ACPL.WIN.0010120051.0.40130367864dbcb-8973-4785-993b-bbeec7c67749014006FM220U015003FPD");
		tlv.tlvmap.keySet().forEach(k -> {
			System.out.println(k+" : "+tlv.get(k));
		});
	}
}
