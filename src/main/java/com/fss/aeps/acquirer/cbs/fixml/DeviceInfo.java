package com.fss.aeps.acquirer.cbs.fixml;

import jakarta.xml.bind.annotation.XmlAttribute;

public class DeviceInfo {

	@XmlAttribute
	public String	dpId;
	@XmlAttribute
	public String	rdsId;
	@XmlAttribute
	public String	rdsVer;
	@XmlAttribute
	public String	dc;
	@XmlAttribute
	public String	mi;
	@XmlAttribute
	public String	mc;
	@XmlAttribute
	public String	error;
	@Override
	public String toString() {
		return "DeviceInfo [dpId=" + dpId + ", rdsId=" + rdsId + ", rdsVer=" + rdsVer + ", dc=" + dc + ", mi=" + mi
				+ ", mc=" + mc + ", error=" + error + "]";
	}



}