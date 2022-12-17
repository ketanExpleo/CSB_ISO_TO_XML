package com.fss.aeps.acquirer.cbs.fixml;

import java.io.ByteArrayInputStream;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "FIXML")
public class FIXMLRequest {
	@XmlElement(name = "Header")
	public RequestHeaderRoot header;

	@XmlElement(name = "Body")
	public RequestBody body;


	@Override
	public String toString() {
		return "FIXML [body=" + body + "]";
	}


	public static void main(String[] args) throws JAXBException {
		String xml= "<?xml version='1.0' encoding='UTF-8'?><FIXML xsi:schemaLocation='http://www.finacle.com/fixml CardManagement.xsd' xmlns='http://www.finacle.com/fixml' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'><Header><RequestHeader><MessageKey><RequestUUID>REQ_0803181200</RequestUUID><ServiceRequestId>AEPSServices</ServiceRequestId><ServiceRequestVersion>10.2</ServiceRequestVersion><ChannelId>COR</ChannelId><LanguageId></LanguageId></MessageKey><RequestMessageInfo><BankId>IPPB</BankId><TimeZone></TimeZone><EntityId></EntityId><EntityType></EntityType><ArmCorrelationId></ArmCorrelationId><MessageDateTime>2018-00-10T15:18:54.167</MessageDateTime></RequestMessageInfo></RequestHeader></Header><Body><Identifier>BalanceInquiry</Identifier><ReplaceIdentifer>N</ReplaceIdentifer><PinCode>100001</PinCode><CardAcceptor>IPPBCBS00000001</CardAcceptor><IIN>607152</IIN><UID>801873492317</UID><TXN_CODE>310000</TXN_CODE><TXN_AMOUNT>000000000000</TXN_AMOUNT><TXN_DATE_TIME>0803181200</TXN_DATE_TIME><STAN>900157</STAN><TIME>181200</TIME><DATE>0803</DATE><MERCHANT_TYPE>6012</MERCHANT_TYPE><ENTRY_MODE>019</ENTRY_MODE><SERVICE_CONDITION>05</SERVICE_CONDITION><ACQURIER_INST_ID>200248</ACQURIER_INST_ID><RRN>258018900157</RRN><TERMINAL_ID>TUSER900</TERMINAL_ID><CARD_ACPT_ID>IPPBCBS00000001</CARD_ACPT_ID><CARD_ACPT_NAME_LOC>DATA CENTER            100001       DLIN</CARD_ACPT_NAME_LOC><CURRENCY_CODE>356</CURRENCY_CODE><MERCHANT_PASS_CODE>52DDDE5FD9DEA997C58FFB7F7AFCB49C10753239F8F86384F196BFFC73385BC</MERCHANT_PASS_CODE><AGENT_DETS>7000045727~BRC~0000~TUSER9</AGENT_DETS><PidData><PidData ><DeviceInfo dpId=\"STARTEK.ACPL\" rdsId=\"ACPL.WIN.001\" rdsVer=\"1.0.4\" dc=\"72958e48-1fee-4ad6-9d59-324f2d816c37\" mi=\"FM220U\" mc=\"MIIDfjCCAmagAwIBAgIDCUHxMA0GCSqGSIb3DQEBCwUAMHQxHTAbBgNVBAMTFEJJSkFZIEFNQVJOQVRIIFNJTkhBMRAwDgYDVQQIEwdHVUpBUkFUMREwDwYDVQQLEwhESVJFQ1RPUjEhMB8GA1UEChMYQUNDRVNTIENPTVBVVEVDSCBQVlQgTFREMQswCQYDVQQGEwJJTjAeFw0yMjA3MzAwNjMxMDRaFw0yMjA4MjkwNjMxMDRaMFExCzAJBgNVBAYTAklOMRAwDgYDVQQIDAdHdWphcmF0MQ0wCwYDVQQKDARVU0VSMQ0wCwYDVQQLDARVU0VSMRIwEAYDVQQDDAlQUk9EX1VTRVIwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDTqEILdBQxww/9Q81t+Gg4F04Zm16HZnfG/bc/JHDLfyl7eN33vZrDHHKTSQrE0FBqNA7mIapDE5QI9ZXdxApKE5ZV7d5ePNx3ex7WupA1wJezv1yzJE7iY2K/OU31KOmqCc1s+Qrw5Mt/YJuUejS+xkMVZS2EMBjRu3qhzY/QyKO6NVq39WzJUSdKaJ9S4Su87xyFBhE7xMbALoQ+Pz2jZbL3BHPT5fOx5CB7Q2KRlgfmONRGsW46/n54CVAZRGDQjHmmFhypJd7pZ/n9IPnWDGK2QGdQcPGfK3zwcHOihoufBWlzbvCKJLoXxw/03nw623slaJ+NCuHKmgHyBidFAgMBAAGjPDA6MAwGA1UdEwQFMAMBAf8wCwYDVR0PBAQDAgGGMB0GA1UdDgQWBBRg2RRzklpN3VE/6LIqVzHSlS/VdTANBgkqhkiG9w0BAQsFAAOCAQEAFQOSznWpW1Q6p5s4mvxbvwXHZxeeAB6inSEwAsph+ueM9384HrYw9zFsckafl0+LW4INKv5U6HR5HMUYSD8TNKLEwf+vNsjjLWodhxPWaYGkfmly+BQlDgwMetEt0f7inYZsamco4gJk5qMODGvLOIMNIu9NZbTdtYEUgsjlVQeD9ioexqJ+M7Qqsz2N3xTKYUhtQKsnusop4hpwYox0sbISd+XgtF2XeyXwJIap7TSpewXH7qN5CoaFeKIZGxNQYjyeS937qcZySMqfWqR8EnZz6I+q5JOEniLA/nQHxolCGSJrRu+Vi1cWSO5UPQ1Kb5Utnjt/hWeGkpXQf2QwMA==\" error=\"\"/><Skey ci=\"20221023\">Pw7AOzDosLOuMqnCky18dwMAf7eYODgX0cEnVNCZ8E3ZsioFDIw+rkFDUSsIDr/+ZaHqZ8Kng8MUmpPT4TUSQey+F2H3h0iHae8yf58J9zn7flOoDUtAqs6X99/eotgfDHW4FohAX6fpAx9+RrUIJnur8GrYxhPeBVgTsh3zF1T2uOFw1YHfI9hT/qZ5SB6tJh+Esn8WD4tS7eL5Cid0M1GsjostMuAZMSkHP5ha0fpQAhvXOlNijxA5CC/Q6Y3WLEUmQmwSoSr19Y6X5PUvvUZvhAHaBtrAT3ct9v95jAVWhzkL/mM2cGDBp58CVKitBGolQ1gbt1h4OjqZFt5S/A==</Skey><Hmac>mx4uCTduShrYZV+hhZ5kWpdudFUZVmyyKmX4QFqebYauEVzK2shnx3eIebrwVjyJ</Hmac><Data type=\"X\">MjAyMi0wOC0wM1QxODoxMTo1OaQGkkJruAxhuSMrIR/QaTq0182XxSf5jcOvWVv/vvC/Y3CFuMMeNZGGomlwEpOm9HDzHZn+vFGt4IsfEftwtufBP4tsBLNeQBDaxfNpxxUCntuD+JtQTS2FPaMvmf3X3eWUPMIROQ8IVZxL/XW8xQ/HlE+BBuenLYYXhuOAN8dWVoAH9H2mmP6EeHNTLJm9N48PsVRZmOO9i4A3o132IbgFTNreWX9zgnK6XnDG5mChgHVggb+CctdmfjHhpwNCoHIknsf5SpPf2YzDKeX7YiRENM3M8fvho4w9eLUPPyQKNKz5zh3L9xsYj7m6x5+WJF6VNwJrshJutyl2rWDEOjkxDxhj5bE3fzNC9K/aBkSTv/CiVh6CAGcP3xy5lKiYQel1bCSjCQctt6V947BWdtAQtf8GZrEeKCNPnFbTD/aP2cXuDeqD1NEsGKyNM8tH1lOokF6V9w1M13fHVOVXFM0Xv+AbGvygxNtiCSllxSVn5S25bN2l9jvXoCrX/5EmVcSRTgeheU8prpCfafNGRZUd7/Q0ZcTsflKiZUx9UDYHwq7P5d46SRtw0K0rkTStlLFNA3HoMFprndWZeUfmCNGO3iBnGTZEvwWMsAQlsd1TuIdgrQCtwgJaPohTaXoFryZd/ysgpdZ3O9iHd7pQTV296CTZGMNlWHpGZPdmKmjs6PgCDdQTZKMFJIwBcIn3sCD0/nCEwwN9gtiPMIsKjuMoCLnBVVr56QtzuEr5+TXGRpjiWHcvCzJDAlBheF7L2kf5R9Af40iphF1tziHUMBKdjsthiKipng5gizaxNazowkxmxcWhHmoAw9bOnInqC3dbRYNi4eN6yOqCMRcuvEOfC8oNBCMkHA7AdyXj4/Ys9Cxo8aO0BXUMtyUHePjCOzO8P5Z2RzQWCMdoe78V7JYLvYoyaytoEyzu24jCKO9nac4vWU7UM44g+3coDrMOQq79L87QQAGTonVhDFyY2JC9k9ecd4wNV95lWnurh+7VxNwCdfqseC58iVNQLHy6A9f0QeLn9ZBEwjS1WfbqcsjbBw+mDBEzKU8zZYX72Ry831tAJQJo8g0CdulrQNA0w2jhkPmbVOhQ+4VC3Rmx5KmHNgY5AFJUg1PEXR3IfdQDM7G1biA=</Data></PidData></PidData></Body></FIXML>";
		JAXBContext context = JAXBContext.newInstance(FIXMLRequest.class);
		FIXMLRequest fixml = (FIXMLRequest) context.createUnmarshaller().unmarshal(new ByteArrayInputStream(xml.getBytes()));
		System.out.println(fixml);
	}
}