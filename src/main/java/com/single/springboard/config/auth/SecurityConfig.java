package com.single.springboard.config.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/css/**", "/images/**",
                                "js/**", "h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                .logout(logoutConfigurer ->
                        logoutConfigurer.logoutSuccessUrl("/"))
                .oauth2Login(oAuth -> {
                    oAuth.defaultSuccessUrl("/");
                    oAuth.userInfoEndpoint(config -> config.userService(customOAuth2UserService));
                });

        return http.build();
    }


}