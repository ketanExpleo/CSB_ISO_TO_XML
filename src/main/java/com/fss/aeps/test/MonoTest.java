package com.fss.aeps.test;

import java.net.Socket;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;

import com.fss.aeps.acquirer.merchant.MerchantTransaction;

import reactor.core.publisher.Mono;

public class MonoTest implements CommandLineRunner{

	@Autowired
	private ApplicationContext context;

	private static final Logger logger = LoggerFactory.getLogger(MonoTest.class);

	public static void main(String[] args) throws InterruptedException {
		CompletableFuture<String> future = new CompletableFuture<>();
		Mono<String> mono = Mono.fromFuture(future)
				.timeout(Duration.of(5, ChronoUnit.SECONDS), Mono.just("timeout"))
				.doOnSuccess(s -> future.complete(null));

		mono.subscribe(s -> logger.info("subscibed : "+s));
		Thread.sleep(TimeUnit.SECONDS.toMillis(3));
		future.complete("abcd");
		logger.info("isCompletedExceptionally : "+future.isCompletedExceptionally());
		logger.info("isDone : "+future.isDone());
		logger.info("isCancelled : "+future.isCancelled());
		logger.info("complete : "+future.complete("pqrs"));
	}

	@Override
	public void run(String... args) throws Exception {
		logger.info("************ MerchantTransaction ***************");
		context.getBean(MerchantTransaction.class, (Socket)null).run();
	}
}
