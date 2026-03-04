package com.royalhouse.cms.security.config;

import com.royalhouse.cms.security.userdetails.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@Configuration
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final PersistentTokenRepository persistentTokenRepository;

    public SecurityConfig(CustomUserDetailsService userDetailsService, PersistentTokenRepository persistentTokenRepository) {
        this.userDetailsService = userDetailsService;
        this.persistentTokenRepository = persistentTokenRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/adminlte/**",
                                "/css/**", "/js/**", "/images/**",
                                "/favicon.ico"
                        ).permitAll()
                        .requestMatchers("/login", "/registration").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/admin", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .rememberMe(rm -> rm
                        .key("royal-house-remember-me-key-CHANGE-ME")
                        .rememberMeParameter("remember-me")
                        .tokenValiditySeconds(60 * 60 * 24 * 7)
                        .userDetailsService(userDetailsService)
                        .tokenRepository(persistentTokenRepository)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .deleteCookies("JSESSIONID",  "remember-me")
                        .permitAll()
                )

                .csrf(Customizer.withDefaults());

        return http.build();
    }
}