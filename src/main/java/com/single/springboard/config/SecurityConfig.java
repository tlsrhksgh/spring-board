package com.single.springboard.config;

import com.single.springboard.config.handler.OAuthLoginSuccessHandler;
import com.single.springboard.domain.user.Role;
import com.single.springboard.exception.handler.CustomAccessDeniedHandler;
import com.single.springboard.exception.handler.CustomAuthEntryPointException;
import com.single.springboard.service.user.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomAuthEntryPointException customAuthEntryPointException;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final OAuthLoginSuccessHandler oAuthLoginSuccessHandler;

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
                .formLogin(FormLoginConfigurer::disable)
                .logout(logoutConfigurer ->
                        logoutConfigurer
                                .logoutSuccessUrl("/")
                                .clearAuthentication(true)
                                .invalidateHttpSession(true)
                )
                .oauth2Login(oAuth2LoginConfigurer -> {
                    oAuth2LoginConfigurer.userInfoEndpoint(config -> config.userService(customOAuth2UserService));
                    oAuth2LoginConfigurer.successHandler(oAuthLoginSuccessHandler);
                })
                .exceptionHandling(exceptionHandlingConfigurer ->
                        exceptionHandlingConfigurer
                                .accessDeniedHandler(customAccessDeniedHandler)
                                .authenticationEntryPoint(customAuthEntryPointException)
                );

        return http.build();
    }
}