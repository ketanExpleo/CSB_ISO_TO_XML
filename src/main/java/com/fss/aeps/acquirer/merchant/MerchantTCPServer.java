package com.fss.aeps.acquirer.merchant;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fss.aeps.AppConfig;
import com.fss.aeps.Stoppable;

@Component("MerchantTCPServer")
public class MerchantTCPServer implements Runnable, Stoppable, BeanNameAware {

	private static final Logger logger = LoggerFactory.getLogger(MerchantTCPServer.class);

	@Autowired
	private AppConfig appConfig;

	@Autowired
	@Qualifier(value = "threadpool")
	private ThreadPoolExecutor executor;

	private ServerSocket serverSocket;

	private String beanName;

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(appConfig.merchantServerPort);
			logger.info("merchant TCP server started on : "+serverSocket);
			while(true) {
				final Socket socket = serverSocket.accept();
				logger.info("socket connected : "+socket);
				executor.execute(appConfig.context.getBean(MerchantTransaction.class, socket));
			}
		} catch (Exception e) {logger.error("error in merchant tcp server.", e);}
	}

	public void reInitialize(String beanName, AppConfig appConfig) {
		if(serverSocket == null || serverSocket.getLocalPort() != appConfig.merchantServerPort) {
			final ServerSocket closeable = serverSocket;
			try(closeable) {
				serverSocket = new ServerSocket(appConfig.merchantServerPort);
			} catch (Exception e) {logger.error("error reinitializing merchant tcp server.", e);}
		}
	}

	@Override
	public void stop() {
		final ServerSocket closeable = serverSocket;
		try(closeable) {
			serverSocket = null;
			logger.info("stopped merchat TCP server.");
			MerchantTCPServer server = appConfig.context.getBean(MerchantTCPServer.class);
			appConfig.context.removeBeanDefinition(server.beanName);
			logger.info("isSame : "+(appConfig.context.getBean(MerchantTCPServer.class)  == server));
		} catch (Exception e) {logger.error("error reinitializing merchant tcp server.", e);}
	}

	@Override
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
}
