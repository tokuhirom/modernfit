package me.geso.modernfit;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import me.geso.modernfit.annotation.GET;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.assertj.core.api.Assertions.assertThat;

interface Foo {
    @GET("/")
    Mono<String> monoString();

    @GET("/")
    String string();
}

class ReturnTypeTest {
    private static WireMockServer wireMockServer;
    private static Foo foo;

    @BeforeAll
    public static void setup() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();

        wireMockServer.stubFor(WireMock.get("/").willReturn(aResponse()
                .withBody("OK")));

        WebClient webClient = WebClient.builder()
                .baseUrl(wireMockServer.baseUrl())
                .build();
        foo = ModernfitFactory.build(webClient, Foo.class);
    }

    @AfterAll
    public static void end() {
        wireMockServer.stop();
        wireMockServer.shutdownServer();
    }

    @Test
    public void getMonoString() {
        Mono<String> top = foo.monoString();
        assertThat(top.block()).isEqualTo("OK");
    }

    // TODO mock http server
    @Test
    public void getString() {
        String top = foo.string();
        assertThat(top).isEqualTo("OK");
    }
}
