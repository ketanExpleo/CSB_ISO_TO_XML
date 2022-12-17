package com.fss.aeps.http.filters;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.fss.aeps.util.XMLSigner;
import com.fss.aeps.util.XMLUtils;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public final class ClientSignatureInterceptor implements Interceptor {

	@Autowired
	private XMLSigner signer;

	@Override
	public Response intercept(final Chain chain) throws IOException {
		final Request     request     		= chain.request();
		final RequestBody requestBody 		= request.body();
		final Buffer      buffer      		= new Buffer();
		requestBody.writeTo(buffer);
		final String      xml        		= buffer.readUtf8();
		final Document document 			= XMLUtils.stringToDocument(xml);
		final Document signeddocument  		= signer.generateXMLDigitalSignature(document);
		final byte[] signedxml 				= XMLUtils.documentToByteArray(signeddocument);
		final MediaType   mediaType  		= MediaType.parse("application/xml");
		final RequestBody body       		= RequestBody.create(signedxml, mediaType);
		final Request     newRequest 		= request.newBuilder().header("Accept", "application/xml").method(request.method(), body).build();
		Response response = chain.proceed(newRequest);
		return response;
	}


	public static final String nodeToString(final Node node) {
		final StringWriter sw = new StringWriter();
		try {
			final Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.transform(new DOMSource(node), new StreamResult(sw));
		} catch (Exception te) {te.printStackTrace();}
		return sw.toString();
	}


}
