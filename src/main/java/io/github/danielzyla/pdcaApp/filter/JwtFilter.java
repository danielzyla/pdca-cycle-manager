package io.github.danielzyla.pdcaApp.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class JwtFilter implements javax.servlet.Filter {

    private final String secret;

    public JwtFilter(final String secret) {
        this.secret = secret;
    }

    @Override
    public void doFilter(
            final ServletRequest request,
            final ServletResponse response,
            final FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String header = httpServletRequest.getHeader("authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new ServletException("wrong or empty header !");
        } else {
            try {
                String token = header.substring(7);
                Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
                request.setAttribute("claims", claims);
            } catch (Exception e) {
                throw new ServletException("wrong key");
            }
        }
        chain.doFilter(request, response);
    }
}
