package co.uk.negura.workshop_vehicle_api.security;

import co.uk.negura.workshop_vehicle_api.util.ValidateTokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final ValidateTokenUtil validateTokenUtil;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);


    public JwtAuthenticationFilter(ValidateTokenUtil validateTokenUtil) {
        this.validateTokenUtil = validateTokenUtil;
    }

    /**
     * This method is invoked once per request to implement the logic of the JWT authentication filter.
     * @param request The HTTP request object containing the request information.
     * @param response The HTTP response object used to send the response.
     * @param filterChain The filter chain object used to continue with the filter chain.
     * @throws ServletException If an error occurs during the filter processing.
     * @throws IOException If an I/O error occurs during the filter processing.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        if (token != null) {
            Map<String, Object> responseBody = validateTokenAndGetBody(token);
            if (responseBody != null) {
                setAuthentication(request, responseBody);
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the Authorization header in the request.
     * @param request The HTTP request object containing the request information.
     * @return The JWT token extracted from the Authorization header, or null if the token is not found.
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    /**
     * Validates the JWT token using the ValidateTokenUtil and retrieves the response body.
     * @param token The JWT token to validate.
     * @return The response body containing the user details if the token is valid, or null if the token is invalid.
     */
    private Map<String, Object> validateTokenAndGetBody(String token) {
        ResponseEntity<?> responseEntity = validateTokenUtil.validateToken(token);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return (Map<String, Object>) responseEntity.getBody();
        }
        return null;
    }

    /**
     * Sets the authentication details in the SecurityContextHolder based on the user details extracted from the response body.
     * The user's roles are mapped to GrantedAuthority objects and a UserDetails object is created with the username and authorities.
     * A UsernamePasswordAuthenticationToken is then created with the UserDetails object and set in the SecurityContextHolder's context.
     * This effectively authenticates the user for the current request.
     * @param request The HTTP request object containing the request information.
     * @param responseBody The response body containing the user details.
     * @throws JsonProcessingException If an error occurs while processing the JSON response.
     */
    private void setAuthentication(HttpServletRequest request, Map<String, Object> responseBody) throws JsonProcessingException {
        String message = (String) responseBody.get("message");
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> messageMap = objectMapper.readValue(message, new TypeReference<>() {
        });
        Map<String, Object> dataMap = (Map<String, Object>) messageMap.get("data");
        String username = (String) dataMap.get("username");
        List<Map<String, Object>> roles = (List<Map<String, Object>>) dataMap.get("roles");
        List<GrantedAuthority> authorities = roles.stream()
                .flatMap(role -> ((List<Map<String, Object>>) role.get("authorities")).stream())
                .map(authority -> (String) authority.get("name"))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        UserDetails userDetails = User.withUsername(username).authorities(authorities).password("").build();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
