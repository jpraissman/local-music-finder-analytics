package com.thelocalmusicfinder.localmusicfinderanalytics.filters;

import com.thelocalmusicfinder.localmusicfinderanalytics.context.UserContext;
import com.thelocalmusicfinder.localmusicfinderanalytics.services.LoggerService;

import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@Order(2)
@RequiredArgsConstructor
public class BotDetectionFilter extends OncePerRequestFilter {

  private final UserContext userContext;
  private final UserAgentAnalyzer uaa = UserAgentAnalyzer.newBuilder().withCache(10_000).build();
  private final LoggerService logger;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String userAgentString = request.getHeader("User-Agent");

    UserAgent userAgent = uaa.parse(userAgentString == null ? "" : userAgentString);

    boolean isBot = userAgent.getValue("DeviceClass").equals("Robot");
    userContext.setUserAgent(userAgentString);
    userContext.setDeviceClass(userAgent.getValue("DeviceClass"));
    userContext.setBot(isBot);

    if (isBot) {
      logger.warn("Bot detected, throwing FORBIDDEN exception. UserAgent: " + userAgentString);
      response.setStatus(HttpStatus.FORBIDDEN.value());
      return;
    }

    filterChain.doFilter(request, response);
  }
}
