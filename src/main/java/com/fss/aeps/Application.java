package com.fss.aeps;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
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

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	@SuppressWarnings("unused")
	private static final boolean init = init();

	public static void main(String[] args) throws IOException {
		try {
			System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
			System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
			System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
			System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
			System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dumpTreshold", "999999");
			final SpringApplicationBuilder builder = new SpringApplicationBuilder(Application.class);
			builder.initializers(new ApplicationInitializer());
			final ConfigurableApplicationContext context = builder.run();
			logger.info(context.toString());
		} catch (Exception e) {logger.error("error", e);}
	}

	private static final boolean init() {
		System.setProperty("jdk.tls.ephemeralDHKeySize", "2048");
		System.setProperty("jdk.tls.rejectClientInitiatedRenegotiation", "true");
		System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "false");
		System.setProperty("spring.banner.location","banner.txt");
		return false;
	}
}
