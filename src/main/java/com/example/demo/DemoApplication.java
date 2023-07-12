package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

import brave.Tracing;
import brave.propagation.B3Propagation;
import brave.sampler.Sampler;
import io.micrometer.context.ContextRegistry;
import io.micrometer.tracing.CurrentTraceContext;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.brave.bridge.BraveBaggageManager;
import io.micrometer.tracing.brave.bridge.BraveCurrentTraceContext;
import io.micrometer.tracing.brave.bridge.BraveTracer;
import io.micrometer.tracing.contextpropagation.ObservationAwareSpanThreadLocalAccessor;
import jakarta.annotation.PostConstruct;
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

    @Bean
    public Tracing braveTracing(brave.propagation.CurrentTraceContext currentTraceContext) {
        return Tracing.newBuilder()
                .currentTraceContext(currentTraceContext)
                .supportsJoin(false)
                .traceId128Bit(true)
                .propagationFactory(B3Propagation.newFactoryBuilder()
                        .injectFormat(B3Propagation.Format.MULTI)
                        .build())
                .sampler(Sampler.NEVER_SAMPLE)
                .build();
    }

    // @Bean
    // public Tracer tracer(CurrentTraceContext currentTraceContext, Tracing braveTracing) {
    //     brave.Tracer tracer = braveTracing.tracer();
    //     BraveTracer braveTracer = new BraveTracer(tracer, currentTraceContext, new BraveBaggageManager());
    //     ContextRegistry.getInstance().registerThreadLocalAccessor(new ObservationAwareSpanThreadLocalAccessor(braveTracer));
    //     return braveTracer;
    // }

}
