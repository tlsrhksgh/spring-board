package com.single.springboard.config;

import com.single.springboard.config.handler.OAuthLoginSuccessHandler;
import com.single.springboard.domain.user.Role;
import com.single.springboard.service.user.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    @Qualifier("CustomAuthEntryPointException")
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final OAuthLoginSuccessHandler oAuthLoginSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(CsrfConfigurer::disable)
                .headers(httpSecurityHeadersConfigurer ->
                        httpSecurityHeadersConfigurer
                                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**"))
                        .permitAll()
                        .requestMatchers("/", "/css/**", "/images/**", "/js/**",
                                "/posts/find/**", "/search/**", "/actuator/health", "/posts/search",
                                "/api/v1/posts/ranking")
                        .permitAll()
                        .requestMatchers("/api/v1/**", "/comment-list/**", "/post-list/**",
                                "/user/info", "/posts/update/**", "/posts/save")
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
                                .deleteCookies("JSESSIONID")
                                .invalidateHttpSession(true)
                                .logoutSuccessUrl("/")
                )
                .oauth2Login(oAuth2LoginConfigurer -> {
                    oAuth2LoginConfigurer.userInfoEndpoint(config -> config.userService(customOAuth2UserService));
                    oAuth2LoginConfigurer.successHandler(oAuthLoginSuccessHandler);
                })
                .exceptionHandling(exceptionHandlingConfigurer ->
                        exceptionHandlingConfigurer
                                .authenticationEntryPoint(authenticationEntryPoint)
                );

        http.sessionManagement(session -> {
            session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
            session.invalidSessionUrl("/");
            session.sessionFixation().changeSessionId();
        });

        return http.build();
    }
}