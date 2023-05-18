package com.dxvalley.nedajpaymnetbackend.security.config;
import com.dxvalley.nedajpaymnetbackend.security.exception.MissingTokenException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Component //You can use also Service or Repository
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private  final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader=request.getHeader("Authorization");// To get token from the authorization
        final String jwt;
        final String userEmail;

        String path = request.getRequestURI();
        if (path.contains("/api/v1/auth/register")
                || path.contains("/api/v1/auth/authenticate")
                ||path.contains("/api/docs/")
                ||path.contains("/swagger-ui.html")
                ||path.contains("/swagger-ui/")
                ||path.contains("/v3/api-docs/")) {
            filterChain.doFilter(request, response);
            return;
        }
        if(authHeader==null ||!authHeader.startsWith("Bearer ")){
            handleMissingTokenException(response);
//            filterChain.doFilter(request, response);
            return;
        }
        //extract the jwt from header
        jwt=authHeader.substring(7);

        try {
            userEmail=jwtService.extractUsername(jwt);//to get a username from a token
        } catch (MissingTokenException e) {
            handleMissingTokenException(response);
            return;
        } catch (Exception e) {
            handleAuthenticationException(response, e.getMessage());
            return;
        }
        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication()==null){
//            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            UserDetails userDetails= this.userDetailsService.loadUserByUsername(userEmail);
            try {
                if(jwtService.isTokenValid(jwt,userDetails)){
                    UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(
                            userDetails,
                             null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            catch (Exception e) {
                handleAuthenticationException(response, e.getMessage());
                return;
            }

        }
        filterChain.doFilter(request,response);

    }
    private void handleMissingTokenException(HttpServletResponse response) throws IOException {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Missing authorization token");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        OutputStream out = response.getOutputStream();
        out.write(new ObjectMapper().writeValueAsBytes(errorResponse));
        out.flush();
    }
    private void handleAuthenticationException(HttpServletResponse response, String message) throws IOException {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", message);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        OutputStream out = response.getOutputStream();
        out.write(new ObjectMapper().writeValueAsBytes(errorResponse));
        out.flush();
    }

    @ExceptionHandler(MissingTokenException.class)
    public void handleMissingTokenException(MissingTokenException e, HttpServletResponse response) throws IOException {
        handleMissingTokenException(response);
    }
}

