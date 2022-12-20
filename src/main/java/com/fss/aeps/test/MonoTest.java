package com.fss.aeps.test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import reactor.core.publisher.Mono;

public class MonoTest implements CommandLineRunner{

	@Autowired
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
		// TODO Auto-generated method stub
		
	}

}
