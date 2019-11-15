package me.geso.modernfit;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import me.geso.modernfit.annotation.GET;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

interface PathTestClient {
    @GET("/")
    String top();

    @GET("/d/e/e/p")
    String deep();
}

class PathTest {
    private WireMockServer wireMockServer;
    private PathTestClient pathTestClient;

    @BeforeEach
    public void setUp() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(wireMockServer.baseUrl())
                .build();
        pathTestClient = ModernfitFactory.build(webClient, PathTestClient.class);
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
        wireMockServer.shutdownServer();
    }

    @Test
    public void testPath1() {
        wireMockServer.stubFor(WireMock.get("/").willReturn(aResponse()
                .withBody("TOP RESPONSE")));

        assertThat(pathTestClient.top())
                .isEqualTo("TOP RESPONSE");
    }
    @Test
    public void testPath2() {
        wireMockServer.stubFor(WireMock.get("/d/e/e/p").willReturn(aResponse()
                .withBody("DEEP RESPONSE")));

        assertThat(pathTestClient.deep())
                .isEqualTo("DEEP RESPONSE");
    }
}