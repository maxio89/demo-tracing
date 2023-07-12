package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import io.micrometer.context.ContextRegistry;
import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.contextpropagation.ObservationAwareSpanThreadLocalAccessor;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@RestController
@RequestMapping("/tracing")
public class ExampleController {

	@Autowired
	CurrentTraceContext context;
	@Autowired
	WebClient webClient;
	@Autowired
	Tracer tracer;

	@PostConstruct
	void setup() {
		ContextRegistry.getInstance().registerThreadLocalAccessor(new ObservationAwareSpanThreadLocalAccessor(tracer));
	}

	@GetMapping("/webclient")
	public Mono<Void> getWebclient() {
		return Mono.just(1)
				.doOnSuccess(a -> webClient.get()
						.uri("http://www.google.pl/")
						.retrieve()
						.bodyToMono(String.class)
						.doOnNext(next -> {
							/* context is null here -> */ System.out.println(context.context());
						})
						.subscribe())
				.then();
	}

}
