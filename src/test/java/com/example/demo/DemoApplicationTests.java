package com.example.demo;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import io.micrometer.tracing.CurrentTraceContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.reactive.function.client.WebClient;

import com.apple.eawt.Application;

import reactor.core.publisher.Hooks;

@SpringBootTest
@ContextConfiguration(classes=DemoApplication.class, loader=AnnotationConfigContextLoader.class)
class DemoApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    CurrentTraceContext context;

    @Test
    void reproducer() {
        System.out.println("Init: " + Observation.NOOP);
        ObservationThreadLocalAccessor.getInstance().setObservationRegistry(ObservationRegistry.NOOP);
        Hooks.enableAutomaticContextPropagation();
        WebClient.create()
                .get()
                .uri("https://httpbin.org/get")
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(t -> System.out.println(context.context()))
                .block();
    }

}
