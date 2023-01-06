 package com.fss.aeps;

import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11Nio2Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;

import com.fss.aeps.http.filters.SignatureAndLoggingFilter;
import com.fss.aeps.jaxb.HeadType;
import com.fss.aeps.jaxb.ProdType;
import com.fss.aeps.repository.AepsResponseCodesRepository;
import com.fss.aeps.repository.CbsToNpciResponseCodesRepository;
import com.fss.aeps.util.Generator;
import com.fss.aeps.util.Mapper;
import com.fss.aeps.util.XMLSigner;

import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;

/**
 * @author Krishna Telgave
 * https://www.linkedin.com/in/krishnatelgave8983290664/
 * https://github.com/KrishnaST
 */
@Configuration
public class AppConfig {

	private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

	public volatile boolean isShutdowned = false;

	@Autowired
	public AnnotationConfigServletWebServerApplicationContext context;

	@Value("${product.version}")
	public String productVersion;

	@Value("${orgId}")
	public String orgId;

	@Value("${iin}")
	public String iin;

	@Value("${participationCode}")
	public String participationCode;

	@Value("${switchNodeIps}")
	public String switchNodeIps;

	@Value("${npci.protocol}")
	public String npciProtocol;

	@Value("${npci.ips}")
	public String npciIps;

	@Value("${npci.port}")
	public int npciPort;

	@Value("${npci.readTimeout}")
	public int npciReadTimeout;

	@Value("${npci.connectTimeout}")
	public int npciConnectTimeout;

	@Value("${signer.keystore.location}")
	Resource signerKeystoreLocation;

	@Value("${signer.keystore.password}")
	private String signerKeystorePassword;

	@Value("${signer.alias}")
	private String signerKeyAlias;

	@Value("${npci.signer.certificate.location}")
	private Resource npciSignerCertificateLocation;

	@Value("${cbs.baseurl}")
	public String cbsBaseUrl;

	@Value("${cbs.readTimeout}")
	public int cbsReadTimeout;

	@Value("${cbs.connectTimeout}")
	public int cbsConnectTimeout;

	@Value("${server.port:#{0}}")
	public int npciListenPort;

	@Value("${microatm_cbs.acquirer.port:#{0}}")
	public int optionalHttpPort;

	@Value("${merchant.acquirer.port}")
	public int merchantServerPort;

	@Value("${uidai.ac}")
	public String uidaiAUACode;

	@Value("${uidai.sa}")
	public String uidaiSubAUACode;

	@Value("${uidai.lk}")
	public String uidaiLicenseKey;

	@Value("${uidai.rc}")
	public String uidaiConsent;

	@Value("${uidai.tid}")
	public String uidaiTid;

	@Value("${uidai.ver}")
	public String uidaiVersion;

	@Autowired
	private CbsToNpciResponseCodesRepository cbsToNpciResponseCodesRepository;

	@Autowired
	private AepsResponseCodesRepository aepsResponseCodesRepository;


	@Bean("cbsToNpciResponseMapper")
	public Mapper getCbsToNpciResponseMapper() {
		final Map<String, String> map = new HashMap<>();
		cbsToNpciResponseCodesRepository.findAll().forEach(c -> map.put(c.getCbsCode(), c.getNpciCode()));
		logger.info("initializing cbsToNpciResponseMapper with :\r\n" + map);
		return new Mapper(map);
	}

	@Bean("npciResponseDescMapper")
	public Mapper getNpciResponseDescMapper() {
		final Map<String, String> map = new HashMap<>();
		aepsResponseCodesRepository.findAll().forEach(c -> map.put(c.getOnlineRespCode(), c.getDescription()));
		return new Mapper(map);
	}

	@Bean
	public XMLSigner getXMLSigner() {
		return new XMLSigner(signerKeystoreLocation, signerKeystorePassword, signerKeyAlias);
	}

	@Bean(name = "threadpool")
	public ThreadPoolExecutor getExecutor() {
		return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
	}

	@Bean(name = "npciSignerPublicKey")
	public PublicKey getNpciSignerPublicKey() {
		try (final InputStream is = npciSignerCertificateLocation.getInputStream()) {
			final CertificateFactory fact = CertificateFactory.getInstance("X.509");
			final X509Certificate certificate = (X509Certificate) fact.generateCertificate(is);
			return certificate.getPublicKey();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	@Bean
	public TomcatServletWebServerFactory servletContainer(@Autowired(required = false) Connector connector) {
		final TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
		if(connector != null) tomcat.addAdditionalTomcatConnectors(connector);
		return tomcat;
	}

	@Bean("httpConnector")
	@ConditionalOnExpression("'${server.http.port}' != '0'")
	public Connector getAcquirerConnector() {
		logger.info("http service starting on optionalHttpPort provided  : " + optionalHttpPort);
		final Connector connector = new Connector(new Http11Nio2Protocol());
		connector.setScheme("http");
		connector.setPort(optionalHttpPort);
		connector.setSecure(false);
		return connector;
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public HeadType getHead() {
		final HeadType head = new HeadType();
		head.setMsgId(Generator.newRandomTxnId(participationCode));
		head.setOrgId(orgId);
		head.setTs(new Date());
		head.setVer(productVersion);
		head.setProdType(ProdType.AEPS);
		return head;
	}

	public void printRegisteredFilters() {
		ServletContext servletContext = context.getBean(ServletContext.class);
		Map<String, ? extends FilterRegistration> filters = servletContext.getFilterRegistrations();
		filters.forEach((k, v) -> {
			logger.info("***** " + k + " : " + context.getBean(k, OrderedFilter.class).getOrder());
		});
	}

	@EventListener(classes = ApplicationReadyEvent.class)
	public void runOnStarted() {
		//getExecutor().execute(context.getBean(MerchantOnusTest.class));
	}

	public FilterRegistrationBean<SignatureAndLoggingFilter> getSignatureFilter(SignatureAndLoggingFilter filter) {
		FilterRegistrationBean<SignatureAndLoggingFilter> bean = new FilterRegistrationBean<>();
		bean.setFilter(filter);
		bean.setUrlPatterns(Arrays.asList("/*"));// "/aeps",
		bean.setOrder(Integer.MIN_VALUE);
		return bean;
	}

	public boolean logBody() {
		return isShutdowned;
	}

	public boolean auditLog() {
		return isShutdowned;
	}

	public boolean auditLogBody() {
		return isShutdowned;
	}
}
