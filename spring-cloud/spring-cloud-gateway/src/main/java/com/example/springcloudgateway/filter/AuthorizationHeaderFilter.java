package com.example.springcloudgateway.filter;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
    Environment env;

    public AuthorizationHeaderFilter(Environment env) {
        super(Config.class);
        this.env = env;
    }

    public static class Config {

    }

    // login을 하면 -> token을 받는다 -> 서버 정보 요구 시 user (with token) -> header (token 있는지 확인)
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // Api를 호출 할 때, 사용자가 로그인을 했을 때 받았던 헤더의 값들을 전달해 주는 작업
            ServerHttpRequest request = exchange.getRequest();

            // HttpHeaders에 인증된 객체가 없다면 에러 리턴
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No Authorization Header", HttpStatus.UNAUTHORIZED);
            }

            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeader.replace("Bearer", "");

            // 정상적인 jwt토큰이 아니라면 에러 리턴
            if (!isJwtValid(jwt)) {
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);
        };
    }

    // jwt 토큰이 올바른 토큰인지 확인 하자
    private boolean isJwtValid(String jwt) {
        boolean returnValue = true;

        String subject = null;

        try {
            // yml 파일에 설정해 둔 secret 토큰 으로 복호화 하기
            // subject에 Jwt 파서를 이용하여 jwt 토큰 뜯어보기
            subject = Jwts.parser().setSigningKey(env.getProperty("token.secret"))
                    .parseClaimsJws(jwt).getBody()
                    .getSubject();
        } catch (Exception e) {
            returnValue = false;
        }

        if (subject == null || subject.isEmpty()) {
            returnValue = false;
        }

        return returnValue;
    }

    // webflux 안에서 처리하는 데이터의 단위 Mono(단일 값), Flux(다중 값)
    // client의 요청이 들어왔을 때, 반환시켜주는 작업
    private Mono<Void> onError(ServerWebExchange exchange, String errmsg, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(errmsg);

        return response.setComplete();
    }
}
