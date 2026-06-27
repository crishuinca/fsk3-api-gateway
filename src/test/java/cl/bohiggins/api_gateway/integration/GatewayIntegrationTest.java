package cl.bohiggins.api_gateway.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.github.tomakehurst.wiremock.WireMockServer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class GatewayIntegrationTest {

	private static WireMockServer bffFake = new WireMockServer(wireMockConfig().dynamicPort());

	static {
		bffFake.start();
	}

	@DynamicPropertySource
	static void urlBff(DynamicPropertyRegistry registry) {
		registry.add("test.bff.url", () -> "http://localhost:" + bffFake.port());
	}

	@Autowired
	private WebTestClient client;

	@AfterAll
	static void cerrar() {
		bffFake.stop();
	}

	@BeforeEach
	void prepararBff() {
		bffFake.resetAll();
		bffFake.stubFor(post("/api/v1/auth/login")
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody("{\"token\":\"token-test\",\"usuario\":{\"nombreUsuario\":\"profesor\"}}")));
	}

	@Test
	void login_pasaPorGateway() {
		client.post()
				.uri("/api/v1/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue("{\"identificador\":\"profesor\",\"password\":\"clave123\"}")
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.token").isEqualTo("token-test")
				.jsonPath("$.usuario.nombreUsuario").isEqualTo("profesor");
	}

	@Test
	void rutaDesconocida_404() {
		client.get()
				.uri("/no-existe")
				.exchange()
				.expectStatus().isNotFound();
	}
}
