package dev.matias.flextime.api.config.security;

import dev.matias.flextime.api.services.CompanyService;
import dev.matias.flextime.api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/user/register", "/api/user/login",
                                "/api/company/register", "/api/company/login")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/user").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/company").authenticated()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/company/**")
                        .hasRole("COMPANY")
                        .anyRequest().authenticated())
                .build();
    }

    @Primary
    @Bean(name = "userAuthenticationManager")
    public AuthenticationManager userAuthenticationManager(HttpSecurity http) throws Exception {
        var authManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

        DaoAuthenticationProvider userProvider = new DaoAuthenticationProvider();
        userProvider.setUserDetailsService(userService);
        userProvider.setPasswordEncoder(passwordEncoder());

        authManagerBuilder.authenticationProvider(userProvider);
        return authManagerBuilder.build();
    }

    @Bean(name = "companyAuthenticationManager")
    public AuthenticationManager companyAuthenticationManager(HttpSecurity http) throws Exception {
        var authManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

        DaoAuthenticationProvider companyProvider = new DaoAuthenticationProvider();
        companyProvider.setUserDetailsService(companyService);
        companyProvider.setPasswordEncoder(passwordEncoder());

        authManagerBuilder.authenticationProvider(companyProvider);
        return authManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
