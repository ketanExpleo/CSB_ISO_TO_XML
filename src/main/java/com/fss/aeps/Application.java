package com.fss.aeps;

import java.io.IOException;
import java.util.logging.LogManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//@formatter:off
/**
 * @author Krishna Telgave 
 * https://www.linkedin.com/in/krishnatelgave8983290664/
 * https://github.com/KrishnaST
 */

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
@ServletComponentScan
@EnableJpaRepositories(considerNestedRepositories = true)
public class Application {

	@SuppressWarnings("unused")
	private static final boolean init = init();

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) throws IOException {
		try {
			final SpringApplicationBuilder builder = new SpringApplicationBuilder(Application.class);
			builder.initializers(new ApplicationInitializer());
			builder.run();
			logger.info("application started.");
		} catch (Exception e) {
			logger.error("error", e);
		}
	}

	private static final boolean init() {
		LogManager.getLogManager().reset();
		SLF4JBridgeHandler.install();
		System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
		System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
		System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
		System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
		System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dumpTreshold", "999999");
		System.setProperty("jdk.tls.ephemeralDHKeySize", "2048");
		System.setProperty("jdk.tls.rejectClientInitiatedRenegotiation", "true");
		System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "false");
		System.setProperty("spring.banner.location", "banner.txt");
		return false;
	}
}
