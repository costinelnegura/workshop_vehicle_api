package co.uk.negura.workshop_vehicle_api.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * This method is used to configure the security filter chain.
     * It configures the security filter chain to handle JWT authentication and authorization for the API endpoints.
     * The filter chain is configured to disable CSRF protection and authorize the API endpoints based on the HTTP method and required authorities.
     * The JWT authentication filter is added before the AnonymousAuthenticationFilter to authenticate the user based on the JWT token.
     * The exception handling is configured to return a 403 Forbidden response when the user does not have the required authority to perform an action.
     * @param http The HttpSecurity object used to configure the security filter chain.
     * @return The SecurityFilterChain object representing the security filter chain configuration.
     * @throws Exception If an error occurs during the security filter chain configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("Access Denied: You do not have the required authority to perform this action");
                        })
                )
                .addFilterBefore(jwtAuthenticationFilter, AnonymousAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/v1/vehicle").hasAuthority("USER_DETAILS_WRITE")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/vehicle/{ID}").hasAuthority("USER_DETAILS_WRITE")
                        .requestMatchers(HttpMethod.GET, "/api/v1/vehicle").hasAuthority("USER_DETAILS_READ")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/vehicle").hasAuthority("USER_DETAILS_DELETE")
                        .anyRequest().authenticated());
        return http.build();
    }
}
