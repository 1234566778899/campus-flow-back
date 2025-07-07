package com.upc.campusflow.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                          JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/carrera").permitAll()
                        .requestMatchers("/estudiante/register").permitAll()
                        .requestMatchers("/profesor/register").permitAll()

                        // ⚡ AGREGADO: Rutas de tareas para pruebas (temporal)
                        // Puedes comentar estas líneas cuando implementes autenticación
                        .requestMatchers("/api/campusflow/tareas/**").permitAll()
                        .requestMatchers("/evento/**").permitAll()
                        .requestMatchers("/grupoForo/**").permitAll()
                        .requestMatchers("/publicacion/**").permitAll()
                        .requestMatchers("/nota/**").permitAll()
                        .requestMatchers("/estudiante/**").permitAll()

                        // ⚡ ALTERNATIVA: Si quieres mantener seguridad, descomenta estas líneas:
                        // Tareas - Solo estudiantes y profesores pueden ver/crear tareas
                        // .requestMatchers(HttpMethod.GET, "/api/campusflow/tareas/**")
                        // .hasAnyAuthority("ROLE_ESTUDIANTE", "ROLE_PROFESOR", "ROLE_ADMIN")
                        // .requestMatchers(HttpMethod.POST, "/api/campusflow/tareas")
                        // .hasAnyAuthority("ROLE_ESTUDIANTE", "ROLE_PROFESOR", "ROLE_ADMIN")
                        // .requestMatchers(HttpMethod.PUT, "/api/campusflow/tareas/**")
                        // .hasAnyAuthority("ROLE_ESTUDIANTE", "ROLE_PROFESOR", "ROLE_ADMIN")
                        // .requestMatchers(HttpMethod.DELETE, "/api/campusflow/tareas/**")
                        // .hasAnyAuthority("ROLE_ESTUDIANTE", "ROLE_PROFESOR", "ROLE_ADMIN")

                        // Swagger
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/webjars/**",
                                "/swagger-resources/**"
                        ).permitAll()

                        // Usuarios pueden ver su propia información
                        .requestMatchers(HttpMethod.GET, "/usuarios/**")
                        .hasAnyAuthority("ROLE_ESTUDIANTE", "ROLE_PROFESOR", "ROLE_ADMIN")

                        // Solo ADMIN puede listar todos los usuarios
                        .requestMatchers(HttpMethod.GET, "/usuarios")
                        .hasAuthority("ROLE_ADMIN")

                        // Solo ADMIN puede crear/modificar/eliminar usuarios
                        .requestMatchers(HttpMethod.POST, "/usuarios")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/usuarios/**")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/usuarios/**")
                        .hasAuthority("ROLE_ADMIN")

                        // Otras rutas ADMIN
                        .requestMatchers("/profesor").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/tareas").hasAuthority("ROLE_ADMIN") // Esta es diferente de /api/campusflow/tareas
                        .requestMatchers("/recursos").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/publicacion").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/nota").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/horarios").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/grupoForo").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/evento").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/estudiante-estadística").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/estudiante").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/carrera").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/asignatura").hasAuthority("ROLE_ADMIN")

                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(customAccessDeniedHandler)
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}