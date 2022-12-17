package com.fss.aeps.jaxb.adapter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import com.fss.aeps.jaxb.KeyValue;
import com.fss.aeps.jaxb.ReqPay;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

public class JaxbTest {

	public static void main(String[] args) throws JAXBException {
		 Reflections reflections = new Reflections("com.fss.aeps.jaxb", new SubTypesScanner(false));
		    List<Class<?>> classes = reflections.getSubTypesOf(Object.class)
		      .stream()
		      .filter(c -> !c.getSimpleName().equals("IPayTrans"))
		      .filter(c -> !c.getSimpleName().equals("package-info"))
		      .collect(Collectors.toList());
		    for (Class<?> classz : classes) {
		    	System.out.println("processing : "+classz.getName());
		    	try {
					JAXBContext.newInstance(classz);
				} catch (JAXBException e) {
					e.printStackTrace();
					System.exit(0);
				}
			}
		    JAXBContext context = JAXBContext.newInstance(ReqPay.class);
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    ReqPay reqPay = new ReqPay();
		    reqPay.extras = new ArrayList<>();
		    reqPay.extras.add(new KeyValue("B", "B"));
		    reqPay.extras.add(new KeyValue("A", "A"));
		    context.createMarshaller().marshal(reqPay, baos);
		    System.out.println(new String(baos.toByteArray()));
	}
}
