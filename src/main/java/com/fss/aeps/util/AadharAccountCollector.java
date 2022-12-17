package com.fss.aeps.util;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import com.fss.aeps.jaxb.AccountType.Detail;


public final class AadharAccountCollector implements Collector<Detail, String[], AadharAccount> {

	private static final AadharAccountCollector collector = new AadharAccountCollector();

	public static final AadharAccountCollector getInstance() {
		return collector;
	}

	private AadharAccountCollector() {}

	@Override
	public final Supplier<String[]> supplier() {
		return () -> new String[3];
	}

	@Override
	public final BiConsumer<String[], Detail> accumulator() {
		return (s, d) -> {
			if (d.getName() == null) return;
			switch (d.getName()) {
				case IIN : s[0] = d.getValue();break;
				case UIDNUM : s[1] = d.getValue();break;
				case VID : s[2] = d.getValue();break;
				default : {}
			}
		};
	}

	@Override
	public final BinaryOperator<String[]> combiner() {
		return (s1, s2) -> {
			final String[] s3 = new String[2];
			s3[0] = s1[0] == null ? s2[0] : s1[0];
			s3[1] = s1[1] == null ? s2[1] : s1[1];
			s3[2] = s1[2] == null ? s2[2] : s1[2];
			return s3;
		};

	}

	@Override
	public final Function<String[], AadharAccount> finisher() {
		return s -> {
			boolean isVid = s[2] != null;
			return new AadharAccount(isVid ? s[2] : s[1], s[0], isVid);
		};
	}

	@Override
	public final Set<Characteristics> characteristics() {
		return Set.of(Characteristics.UNORDERED);
	}
}
