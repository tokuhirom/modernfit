package me.geso.modernfit;

import me.geso.modernfit.annotation.GET;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

interface Foo {
    @GET("/")
    Mono<String> top();

    @GET("/")
    String string();
}

class ModernfitFactoryTest {
    @Test
    public void getMonoString() {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://64p.org/")
                .build();
        Foo foo = ModernfitFactory.build(webClient, Foo.class);
        Mono<String> top = foo.top();
        assertThat(top.block()).contains("<html>");
    }

    // TODO mock http server
    @Test
    public void getString() {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://64p.org/")
                .build();
        Foo foo = ModernfitFactory.build(webClient, Foo.class);
        String top = foo.string();
        assertThat(top).contains("<html>");
    }
}
