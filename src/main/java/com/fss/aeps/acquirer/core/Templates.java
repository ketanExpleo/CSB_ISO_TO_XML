package com.fss.aeps.acquirer.core;

import com.fss.aeps.jaxb.DeviceTagNameType;
import com.fss.aeps.jaxb.DeviceType;
import com.fss.aeps.jaxb.DeviceType.Tag;
import com.fss.aeps.jaxb.Uses;

public final class Templates {

	public static final DeviceType getDeviceType() {
		final DeviceType device = new DeviceType();
		device.getTag().add(new Tag(DeviceTagNameType.TYPE, "INET"));
		device.getTag().add(new Tag(DeviceTagNameType.POS_ENTRY_CODE, "019"));
		device.getTag().add(new Tag(DeviceTagNameType.POS_SERV_CDN_CODE, "05"));
		device.getTag().add(new Tag(DeviceTagNameType.CARD_ACCP_TR_ID, "register"));
		return device;
	}

	public static final Uses getFMRandFIRUses(final String bt) {
		final Uses uses = new Uses(); //**
		uses.setPi("n");
		uses.setPa("n");
		uses.setPfa("n");
		uses.setBio("y");
		uses.setBt(bt);
		uses.setPin("n");
		uses.setOtp("n");
		return uses;
	}

	public static final Uses getUses(String bt) {
		final Uses uses = new Uses();
		uses.setPi(""+bt.charAt(0));
		uses.setPa(""+bt.charAt(1));
		uses.setPfa(""+bt.charAt(2));
		uses.setBio(""+bt.charAt(3));
		uses.setBt(bt.substring(4, bt.length()-2));
		uses.setPin(""+bt.charAt(bt.length()-2));
		uses.setOtp(""+bt.charAt(bt.length()-1));
		return uses;
	}

	public static void main(String[] args) {
		Uses uses = Templates.getUses("nnnyFMR,FIRnn");
		System.out.println(uses);
	}
}
