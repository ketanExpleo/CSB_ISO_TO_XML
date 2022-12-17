package com.fss.aeps.util;

import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public final class NetworkUtil {

	public static final List<String> getSystemIpAddresses() {
		final List<String> addresses = new ArrayList<>();
		try {
			final Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
			networks.asIterator().forEachRemaining(interfaces -> {
				interfaces.inetAddresses().filter(i -> !i.isLoopbackAddress()).filter(i -> i instanceof Inet4Address)
				.forEach(i -> addresses.add(i.getHostAddress()));
			});
			return addresses;
		} catch (Exception e) {e.printStackTrace();}
		if(addresses.isEmpty()) throw new RuntimeException("no host address found for this switch node.");
		return addresses;
	}
}
