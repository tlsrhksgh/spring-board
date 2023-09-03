package com.single.springboard.config;

import com.single.springboard.domain.user.Role;
import com.single.springboard.exception.auth.CustomAuthEntryPointException;
import com.single.springboard.service.user.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomAuthEntryPointException customAuthEntryPointException;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(CsrfConfigurer::disable)
                .headers(httpSecurityHeadersConfigurer ->
                        httpSecurityHeadersConfigurer
                                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/css/**", "/images/**", "/h2-console/**",
                                "/js/**", "/posts/find/**", "/search/**", "/actuator/health")
                        .permitAll()
                        .requestMatchers("/api/v1/comments/**", "/api/v1/posts/**")
                        .hasRole(Role.USER.name())
                        .anyRequest().authenticated())
                .anonymous(anonymous -> {
                    anonymous.principal("guestUser")
                            .authorities(Role.GUEST.getKey())
                            .key(Role.GUEST.getKey());
                })
                .logout(logoutConfigurer ->
                        logoutConfigurer
                                .logoutSuccessUrl("/")
                                .invalidateHttpSession(true)
                )
                .oauth2Login(oAuth -> {
                    oAuth.defaultSuccessUrl("/");
                    oAuth.userInfoEndpoint(config -> config.userService(customOAuth2UserService));
                })
                .exceptionHandling(exception -> {
                    exception.authenticationEntryPoint(customAuthEntryPointException);
                });

        http.sessionManagement(management -> {
            management.maximumSessions(2) // 최대 허용 가능한 세션 수
                    .maxSessionsPreventsLogin(true) // 동시 로그인 차단
                    .expiredUrl("/");
        });

        return http.build();
    }

}