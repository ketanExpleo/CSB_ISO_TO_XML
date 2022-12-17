package com.fss.aeps.jaxb;

public class UidaiDataInfo {

	public String	version;
	public String	uidaiToken			= "NA";
	public String	uidType				= "NA";
	public String	sha256Demo			= "NA";
	public String	encodedUsageData	= "NA";
	public String	pidVersion			= "NA";
	public String	timestamp			= "NA";
	public String	fmrCount			= "NA";
	public String	firCount			= "NA";
	public String	iirCount			= "NA";
	public String	fidCount			= "NA";
	public String	authApiVersion		= "NA";
	public String	sha256ASA			= "NA";
	public String	sha256AUA			= "NA";
	public String	sha256SAU			= "NA";
	public String	lang				= "NA";
	public String	pi_ms				= "NA";
	public String	pi_mv				= "NA";
	public String	pi_lmv				= "NA";
	public String	pa_ms				= "NA";
	public String	pa_mv				= "NA";
	public String	pa_lmv				= "NA";
	public String	pfa_ms				= "NA";
	public String	pfa_mv				= "NA";
	public String	pfa_lmv				= "NA";
	public String	tid					= "NA";
	public String	rdsId				= "NA";
	public String	rdsVer				= "NA";
	public String	dpId				= "NA";
	public String	mi					= "NA";
	public String	rdLevel				= "NA";
	public String	wadh				= "NA";

	public static final UidaiDataInfo parse(String string) {
		UidaiDataInfo info = new UidaiDataInfo();
		String[] token = string.split("[,\\{\\}]");
		info.version = token[0];
		info.uidaiToken = token[1];
		info.uidType = token[2];
		info.sha256Demo = token[3];
		info.encodedUsageData = token[4];
		info.pidVersion = token[5];
		info.timestamp = token[6];
		info.fmrCount = token[7];
		info.firCount = token[8];
		info.iirCount = token[9];
		info.fidCount = token[10];
		info.authApiVersion = token[11];
		info.sha256ASA = token[12];
		info.sha256AUA = token[13];
		info.sha256SAU = token[14];
		info.lang = token[15];
		info.pi_ms = token[16];
		info.pi_mv = token[17];
		info.pi_lmv = token[18];
		info.pa_ms = token[19];
		info.pa_mv = token[20];
		info.pa_lmv = token[21];
		info.pfa_ms = token[22];
		info.pfa_mv = token[23];
		info.pfa_lmv = token[24];
		info.tid = token[25];
		info.rdsId = token[26];
		info.rdsVer = token[27];
		info.dpId = token[28];
		info.mi = token[29];
		info.rdLevel = token[30];
		info.wadh = token[31];
		return info;
	}

	@Override
	public final String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(version).append("{");
		sb.append(uidaiToken).append(",");
		sb.append(uidType).append(",");
		sb.append(sha256Demo).append(",");
		sb.append(encodedUsageData).append(",");
		sb.append(pidVersion).append(",");
		sb.append(timestamp).append(",");
		sb.append(fmrCount).append(",");
		sb.append(firCount).append(",");
		sb.append(iirCount).append(",");
		sb.append(fidCount).append(",");
		sb.append(authApiVersion).append(",");
		sb.append(sha256ASA).append(",");
		sb.append(sha256AUA).append(",");
		sb.append(sha256SAU).append(",");
		sb.append(lang).append(",");
		sb.append(pi_ms).append(",");
		sb.append(pi_mv).append(",");
		sb.append(pi_lmv).append(",");
		sb.append(pa_ms).append(",");
		sb.append(pa_mv).append(",");
		sb.append(pa_lmv).append(",");
		sb.append(pfa_ms).append(",");
		sb.append(pfa_mv).append(",");
		sb.append(pfa_lmv).append(",");
		sb.append(tid).append(",");
		sb.append(rdsId).append(",");
		sb.append(rdsVer).append(",");
		sb.append(dpId).append(",");
		sb.append(mi).append(",");
		sb.append(rdLevel).append(",");
		sb.append(wadh).append("}");
		return sb.toString();
	}

	public static void main(String[] args) {
		String data = "04{01111116ekDc4hZDdYqyR5wpGvwVatsSq3DLxyMpzzU4cEeLIqdwhMKvhcbgOxlhySIGX2Hg,A,e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855,0100002000000210,2.0,20220721170748,1,0,0,0,2.5,df5bffab9001baf50e83059d18f359203299fda12e0adc5f7362b638cc77e156,df1f6dab559b4f8b2f4e08d6bff36af887d3f20021efb5bc64a2b2fe0406d984,df1f6dab559b4f8b2f4e08d6bff36af887d3f20021efb5bc64a2b2fe0406d984,NA,NA,NA,NA,NA,NA,NA,NA,NA,NA,registered,ACPL.AND.001,2.0.4,STARTEK.ACPL,FM220U,L0,NA}";
		UidaiDataInfo info = UidaiDataInfo.parse(data);
		System.out.println(data.equals(info.toString()));
	}

}
