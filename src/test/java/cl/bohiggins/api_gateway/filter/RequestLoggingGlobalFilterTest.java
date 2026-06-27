package cl.bohiggins.api_gateway.filter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class RequestLoggingGlobalFilterTest {

	@Mock
	private GatewayFilterChain chain;

	@InjectMocks
	private RequestLoggingGlobalFilter filter;

	@Test
	void filter_delegaCadenaYCompleta() {
		MockServerWebExchange exchange = MockServerWebExchange.from(
				MockServerHttpRequest.get("http://localhost:8080/api/v1/auth/login").build());
		exchange.getResponse().setStatusCode(HttpStatus.OK);

		when(chain.filter(any())).thenReturn(Mono.empty());

		filter.filter(exchange, chain).block();

		verify(chain).filter(exchange);
	}
}
