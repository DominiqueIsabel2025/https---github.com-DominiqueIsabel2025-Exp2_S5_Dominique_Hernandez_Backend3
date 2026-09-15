package cl.duoc.bffatm.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticacion simple para el canal Cajero Automatico.
 * Simula la validacion de una tarjeta + PIN mediante un header
 * "X-ATM-KEY", ya que este canal maneja operaciones criticas
 * (retiros y consultas de saldo) y requiere control de acceso propio,
 * independiente del que pudieran usar Web o Movil.
 */
@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    @Value("${atm.security.api-key}")
    private String expectedApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (!request.getRequestURI().startsWith("/atm/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader("X-ATM-KEY");
        if (apiKey == null || !apiKey.equals(expectedApiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Acceso no autorizado al canal Cajero Automatico\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
