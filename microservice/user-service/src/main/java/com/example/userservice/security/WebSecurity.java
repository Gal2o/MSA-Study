package com.example.userservice.security;

import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.Filter;

// Configuration - 다른 어노테이션 보다 우선순위가 우선이다.
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Environment env;

    // 권한에 관련된 부분
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.authorizeRequests().antMatchers("/**")
                .permitAll()
//                .hasIpAddress("192.168.0.70")
                .and()
                .addFilter(getAuthenticationFilter());

        // http 의 컴포넌트 들을 프레임 단위로 차단, -> disable 하면 다시 프레임 단위로 컴포넌트를 읽어온다.
        http.headers().frameOptions().disable();
    }

    // 인증에 관련된 부분
    // select pwd from users where email=?
    // (encrpyt) db_pwd == input_pwd (비교 하려면 input_pwd 도 암호화 해줘야 한다.)
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        // 로그인 시도 시, AuthenticationFilter 클래스가 가장 먼저 호출
        // 생성자를 통해 객체 생성
        // authenticationManager() 은 인증이 성공하였으면, 인증된 객체를 다시 돌려주는 역할
        AuthenticationFilter authenticationFilter =
                new AuthenticationFilter(authenticationManager(), userService, env);

        // 기본 생성자는 필요없다.
//        authenticationFilter.setAuthenticationManager(authenticationManager());

        return authenticationFilter;
    }
}
