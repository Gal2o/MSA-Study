package com.example.userservice.security;

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private UserService userService;
    private Environment env;

    public AuthenticationFilter(AuthenticationManager authenticationManager,
                                UserService userService,
                                Environment env) {
        super.setAuthenticationManager(authenticationManager);
        this.userService = userService;
        this.env = env;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        try {
            RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);

            // 입력받은 email, password를 -> creds (RequestLogin) 에 매핑
            // 토큰으로 만들어서 인증 처리 하는 부분에 넘긴다.
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>()
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
//        log.debug(((User)authResult.getPrincipal()).getUsername());

        // 생성된 계정의 userName을 활용하여
        String userName = ((User)authResult.getPrincipal()).getUsername();

        // 토큰을 만들기 위해 userName으로 계정의 필요한 정보를 가져온다
        UserDto userDetails = userService.getUserDetailsByEmail(userName);

        // Jwt Token을 만들자
        String token = Jwts.builder()
                .setSubject(userDetails.getUserId()) // token 제목 (Header)
                .setExpiration(new Date(System.currentTimeMillis() +
                        Long.parseLong(env.getProperty("token.expiration_time")))) // token의 유효기간 (현재 시간 + 24시간)
                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret")) // 암호화 알고리즘, 암호화 할 키
                .compact();

        // responseHeader에 "token"에 정상적으로 만들어진 것인지 확인하기 위해 userId도 같이 넣어줌
        response.addHeader("token", token);
        response.addHeader("userId", userDetails.getUserId());
    }
}
