package co.uk.negura.workshop_vehicle_api.security;

import co.uk.negura.workshop_vehicle_api.util.ValidateTokenUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    public JwtAuthenticationFilter(ValidateTokenUtil validateTokenUtil) {
        this.validateTokenUtil = validateTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            // Validate the token and extract the user details
            ResponseEntity<?> responseEntity = validateTokenUtil.validateToken(token);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = (Map<String, Object>) responseEntity.getBody();
                /*
                Example of responseBody:
                responseBody=
                {
                  "status": "200 OK",
                  "message": {
                    "data": {
                      "iss": "workshop_ltd",
                      "sub": "17",
                      "aud": ["workshop_user"],
                      "exp": 1711137616,
                      "iat": 1711051216,
                      "jti": "59c9b0a4-1577-4005-b7c4-c02c12788153",
                      "userId": 17,
                      "email": "test10@test17.com",
                      "username": "testtest123455335",
                      "roles": [
                        {
                          "name": "ADMIN",
                          "authorities": [
                            {
                              "name": "USER_DETAILS_READ",
                              "id": 1
                            },
                            {
                              "name": "USER_DETAILS_WRITE",
                              "id": 2
                            }
                          ],
                          "id": 2
                        }
                      ]
                    },
                    "message": "Token is valid",
                    "status": 200
                  }
                }
                 */
                String message = (String) responseBody.get("message");
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> messageMap = objectMapper.readValue(message, new TypeReference<Map<String, Object>>(){});
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
        filterChain.doFilter(request, response);
    }
}
