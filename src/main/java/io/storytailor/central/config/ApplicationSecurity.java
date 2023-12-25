package io.storytailor.central.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class ApplicationSecurity {
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            Properties properties)
            throws Exception {
        http.csrf().disable().cors().and().headers().frameOptions().disable().and().formLogin().disable().httpBasic().disable().logout().disable().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests().requestMatchers("/api/login/*").permitAll().anyRequest().authenticated();
        return http.build();
    }
}