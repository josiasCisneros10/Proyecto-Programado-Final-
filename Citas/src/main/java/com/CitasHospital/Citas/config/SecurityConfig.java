package com.CitasHospital.Citas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import com.CitasHospital.Citas.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(PathPatternRequestMatcher.pathPattern("/h2-console/**")))
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/login",
                                "/registro",
                                "/recuperar-password",
                                "/cambiar-password",
                                "/css/**",
                                "/h2-console/**")
                        .permitAll()
                        .requestMatchers(
                                "/usuarios/**",
                                "/medicos/nuevo",
                                "/medicos/guardar",
                                "/medicos/editar/**",
                                "/medicos/desactivar/**",
                                "/disponibilidades/nueva",
                                "/disponibilidades/guardar",
                                "/disponibilidades/editar/**",
                                "/disponibilidades/ocupar/**",
                                "/disponibilidades/liberar/**",
                                "/disponibilidades/eliminar/**",
                                "/admin/**")
                        .hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/inicio", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .userDetailsService(customUserDetailsService);

        return http.build();
    }
}
