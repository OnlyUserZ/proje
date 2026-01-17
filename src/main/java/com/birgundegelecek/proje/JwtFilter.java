package com.birgundegelecek.proje;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        log.debug("Request Path: {}", path);

        if (path.startsWith("/auth/login") ||
            path.startsWith("/auth/refresh") ||
            path.startsWith("/auth/register") ||
            path.startsWith("/api/kategori/goster") ||
            path.startsWith("/api/sorun/kategori")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        log.debug("Authorization header: {}", authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Token eksik veya hatalı.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token eksik veya hatalı.");
            return;
        }

        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            if (tokenBlacklistService.isBlacklisted(token)) {
                log.warn("Token kara listede.");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token kara listede.");
                return;
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.isAccessTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("Authentication başarılı. username={}", username);
                } else {
                    log.warn("Token geçersiz. username={}", username);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token geçersiz.");
                    return;
                }
            }

        } catch (ExpiredJwtException e) {
            log.warn("Token süresi dolmuş.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token süresi dolmuş.");
            return;
        } catch (MalformedJwtException e) {
            log.warn("Token hatalı/malformed.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token geçersiz.");
            return;
        } catch (Exception e) {
            log.error("Token doğrulama sırasında hata: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token doğrulanamadı.");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
