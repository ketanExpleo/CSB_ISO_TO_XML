package com.fss.aeps.util;

public class AadharAccount {

	public final String uidVid;
	public final String iin;
	public boolean isVId;

	public AadharAccount(String uidVid, String iin, boolean isVId) {
		this.uidVid = uidVid;
		this.iin = iin;
		this.isVId = isVId;
	}

	@Override
	public String toString() {
		return uidVid+"@"+iin+".aadhar.npci";
	}

}
