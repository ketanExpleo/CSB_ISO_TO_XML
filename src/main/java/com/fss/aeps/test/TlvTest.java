package com.fss.aeps.test;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class TlvTest {

	public static void main(String[] args) throws SocketException {
		Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
		networks.asIterator().forEachRemaining(i -> {
			i.getInetAddresses().asIterator().forEachRemaining(inet -> {
				System.out.println("inet.getHostAddress() : "+inet.getHostAddress());
				System.out.println("inet.getHostName() : "+inet.getHostName());
				System.out.println("inet.getAddress() : "+inet.getAddress());
				System.out.println("inet.isAnyLocalAddress() : "+inet.isAnyLocalAddress());
				System.out.println("inet.isLinkLocalAddress() : "+inet.isLinkLocalAddress());
				System.out.println("inet.isLoopbackAddress() : "+inet.isLoopbackAddress());
			});
		});
	}
}
