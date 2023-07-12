package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import io.micrometer.tracing.CurrentTraceContext;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/tracing")
public class ExampleController {

	@Autowired
	CurrentTraceContext context;
	@Autowired
	WebClient webClient;

	@GetMapping("/webclient")
	public Mono<Void> getWebclient() {
		return Mono.just(1)
				.doOnSuccess(a -> WebClient.create().get()
						.uri("https://httpbin.org/get")
						.retrieve()
						.bodyToMono(String.class)
						.doOnNext(next -> {
							System.out.println(context.context());
						})
						.subscribe())
				.then();
	}

}
