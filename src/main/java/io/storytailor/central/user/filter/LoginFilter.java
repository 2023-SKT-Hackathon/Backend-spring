package io.storytailor.central.user.filter;

import io.storytailor.central.user.service.LoginSVC;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends OncePerRequestFilter {

  private final LoginSVC loginSVC;

  private static final List<String> EXCLUDE_URLS = Collections.unmodifiableList(
    Arrays.asList("/api/login/url", "/api/login/callback/kakao")
  );

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {
    final String authorization = loginSVC.resolveToken(request);
    if (shouldExclude(request)) {
      filterChain.doFilter(request, response);
      return;
    } else {
      if (authorization != null && loginSVC.validateToken(authorization)) {
        String name = loginSVC.getUserInfo(authorization);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
          name,
          null,
          List.of(new SimpleGrantedAuthority("user"))
        );

        authenticationToken.setDetails(
          new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder
          .getContext()
          .setAuthentication(authenticationToken);
      } else {
        log.error("Fail to validate token");
        response.sendError(401, authorization);
        return;
      }
      filterChain.doFilter(request, response);
    }
  }

  private boolean shouldExclude(HttpServletRequest request) {
    return EXCLUDE_URLS
      .stream()
      .anyMatch(url -> request.getRequestURI().contains(url));
  }
}
