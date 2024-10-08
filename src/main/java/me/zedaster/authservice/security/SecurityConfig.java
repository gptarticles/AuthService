package me.zedaster.authservice.security;

import lombok.AllArgsConstructor;
import me.zedaster.authservice.security.auth.UsernameOrEmailAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration for Spring Security.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {
    /**
     * Custom user details service.
     */
    private final IdUserDetailsService idUserDetailsService;

    /**
     * Custom authentication filter.
     */
    private final JwtAuthFilter jwtAuthFilter;

    /**
     * Returns security filter chain.
     * @return Security filter chain.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated())
                .build();
    }

    /**
     * Returns customized user details service.
     * @return Custom user details service.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return idUserDetailsService;
    }

    /**
     * Returns authentication provider with user details service and password encoder.
     * @return Authentication provider.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Returns password encoder.
     * @return Password encoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(5);
    }

    /**
     * Returns authentication manager.
     * @param httpSecurity Http security configuration.
     * @param authProvider Custom authentication provider that supports both username and email.
     * @return Authentication manager.
     * @throws Exception If something goes wrong
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity, UsernameOrEmailAuthenticationProvider authProvider) throws Exception {
        return httpSecurity
                .getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(authProvider)
                .build();
    }
}
