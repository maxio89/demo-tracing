package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.brave.bridge.BraveCurrentTraceContext;
import reactor.core.publisher.Hooks;

@SpringBootApplication
public class DemoApplication {

	static {
		Hooks.enableAutomaticContextPropagation();
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public CurrentTraceContext currentTraceContext(brave.propagation.CurrentTraceContext currentTraceContext) {
		return new BraveCurrentTraceContext(currentTraceContext);
	}

	@Bean
	WebClient webClient(WebClient.Builder builder) {
		return builder.build();
	}

}
