package com.genealogy.genealogryu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        var user = User.withUsername("user").password(passwordEncoder.encode("user1234")).roles("USER").build();
        var admin = User.withUsername("admin").password(passwordEncoder.encode("admin1234")).roles("ADMIN").build();
        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                // 인증 필요(작성/수정/삭제)
                .requestMatchers(HttpMethod.GET, "/notices/create", "/notices/*/edit").authenticated()
                .requestMatchers(HttpMethod.POST, "/notices/create", "/notices/*/edit", "/notices/*/delete").authenticated()
                .requestMatchers(HttpMethod.GET, "/events/create", "/events/*/edit").authenticated()
                .requestMatchers(HttpMethod.POST, "/events/create", "/events/*/edit", "/events/*/delete").authenticated()

                // 공개 경로
                .requestMatchers("/login").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()
                .requestMatchers(HttpMethod.GET, "/", "/notices", "/notices/**", "/notices/search",
                        "/notices/important", "/notices/popular").permitAll()
                .requestMatchers(HttpMethod.GET, "/events", "/events/**", "/events/search",
                        "/events/important", "/events/popular").permitAll()

                // 그 외
                .anyRequest().authenticated()
        ).formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/notices", true)
                .permitAll()
        ).logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/notices")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
        ).csrf(Customizer.withDefaults());

        return http.build();
    }
}
