//package com.projekt.cinemabooking.config;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.projekt.cinemabooking.entity.User;
//import com.projekt.cinemabooking.security.CustomUserDetails;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Configuration
//@RequiredArgsConstructor
//@EnableMethodSecurity
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        http
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/auth/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/movies/**", "/api/screenings/**").permitAll()
//                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/h2-console/**").permitAll()
//
//                        // Admin only
//                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.POST, "/api/movies/**", "/api/screenings/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")
//
//                        .anyRequest().authenticated()
//                )
//                .formLogin(form -> form
//                        .loginProcessingUrl("/api/auth/login")
//                        .successHandler((request, response, authentication) -> {
//                            response.setStatus(HttpServletResponse.SC_OK);
//                            response.setContentType("application/json");
//                            response.setCharacterEncoding("UTF-8");
//
//                            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//                            User user = userDetails.getUser();
//
//                            Map<String, Object> result = new HashMap<>();
//                            result.put("message", "Zalogowano pomyślnie");
//                            result.put("user", convertUserToMap(user));
//
//                            response.getWriter().write(objectMapper.writeValueAsString(result));
//                        })
//                        .failureHandler((request, response, exception) -> {
//                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                            response.setContentType("application/json");
//                            response.setCharacterEncoding("UTF-8");
//
//                            Map<String, String> error = new HashMap<>();
//                            error.put("error", "Błędny login lub hasło");
//
//                            response.getWriter().write(objectMapper.writeValueAsString(error));
//                        })
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .logoutUrl("/api/auth/logout")
//                        .logoutSuccessHandler((request, response, authentication) -> {
//                            response.setStatus(HttpServletResponse.SC_OK);
//                            response.setContentType("application/json");
//                            response.setCharacterEncoding("UTF-8");
//
//                            Map<String, String> result = new HashMap<>();
//                            result.put("message", "Wylogowano pomyślnie");
//
//                            response.getWriter().write(objectMapper.writeValueAsString(result));
//                        })
//                        .permitAll()
//                )
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
//                )
//                .exceptionHandling(e -> e
//                        .authenticationEntryPoint((request, response, authException) -> {
//                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                            response.setContentType("application/json");
//                            response.getWriter().write("{\"error\": \"Nie jesteś zalogowany\"}");
//                        })
//                )
//                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)); // Dla H2 Console
//
//        return http.build();
//    }
//
//    private Map<String, Object> convertUserToMap(User user) {
//        Map<String, Object> userMap = new HashMap<>();
//        userMap.put("id", user.getId());
//        userMap.put("email", user.getEmail());
//        userMap.put("firstName", user.getFirstName());
//        userMap.put("lastName", user.getLastName());
//
//        List<Map<String, String>> roles = user.getRoles().stream()
//                .map(role -> {
//                    Map<String, String> roleMap = new HashMap<>();
//                    roleMap.put("name", role.getName());
//                    return roleMap;
//                })
//                .collect(Collectors.toList());
//
//        userMap.put("roles", roles);
//        return userMap;
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
//        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
//        configuration.setAllowedHeaders(List.of("*"));
//        configuration.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//}

package com.projekt.cinemabooking.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projekt.cinemabooking.entity.User;
import com.projekt.cinemabooking.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/login", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/movies/**", "/api/screenings/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/h2-console/**").permitAll()

                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/movies/**", "/api/screenings/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/api/auth/login")

                        .successHandler((request, response, authentication) -> {
                            if (request.getRequestURI().startsWith("/api") || "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                                response.setStatus(HttpServletResponse.SC_OK);
                                response.setContentType("application/json;charset=UTF-8");

                                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                                Map<String, Object> result = new HashMap<>();
                                result.put("message", "Zalogowano pomyślnie");
                                result.put("user", convertUserToMap(userDetails.getUser()));

                                response.getWriter().write(objectMapper.writeValueAsString(result));
                            } else {
                                new SavedRequestAwareAuthenticationSuccessHandler().onAuthenticationSuccess(request, response, authentication);
                            }
                        })
                        .failureHandler((request, response, exception) -> {
                            if (request.getRequestURI().startsWith("/api")) {
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.setContentType("application/json;charset=UTF-8");
                                response.getWriter().write("{\"error\": \"Błędny login lub hasło\"}");
                            } else {
                                response.sendRedirect("/login?error");
                            }
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", HttpMethod.GET.name()))
                        .logoutSuccessHandler((request, response, authentication) -> {
                            if (request.getRequestURI().startsWith("/api")) {
                                response.setStatus(HttpServletResponse.SC_OK);
                                response.setContentType("application/json;charset=UTF-8");
                                response.getWriter().write("{\"message\": \"Wylogowano pomyślnie\"}");
                            } else {
                                response.sendRedirect("/login?logout");
                            }
                        })
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .exceptionHandling(e -> e
                        .defaultAuthenticationEntryPointFor(
                                (request, response, authException) -> {
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    response.setContentType("application/json");
                                    response.getWriter().write("{\"error\": \"Nie jesteś zalogowany\"}");
                                },
                                new AntPathRequestMatcher("/api/**")
                        )
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new AntPathRequestMatcher("/**")
                        )
                )
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();
    }

    private Map<String, Object> convertUserToMap(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("email", user.getEmail());
        userMap.put("firstName", user.getFirstName());
        userMap.put("lastName", user.getLastName());
        userMap.put("isAdmin", user.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN")));

        List<Map<String, String>> roles = user.getRoles().stream()
                .map(role -> Map.of("name", role.getName()))
                .collect(Collectors.toList());

        userMap.put("roles", roles);
        return userMap;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}