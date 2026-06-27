package cl.bohiggins.api_gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class RequestLoggingGlobalFilter implements GlobalFilter, Ordered {

	private static final Logger log = LoggerFactory.getLogger(RequestLoggingGlobalFilter.class);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		long inicio = System.currentTimeMillis();
		String metodo = exchange.getRequest().getMethod().name();
		String path = exchange.getRequest().getURI().getPath();
		boolean tieneAuth = exchange.getRequest().getHeaders().containsKey("Authorization");

		log.info("Gateway recibe {} {} (auth={})", metodo, path, tieneAuth);

		return chain.filter(exchange).then(Mono.fromRunnable(() -> {
			HttpStatusCode status = exchange.getResponse().getStatusCode();
			long duracion = System.currentTimeMillis() - inicio;
			log.info("Gateway responde {} {} -> {} ({} ms)", metodo, path, status != null ? status.value() : "?", duracion);
		}));
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}
}
