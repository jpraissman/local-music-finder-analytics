package com.thelocalmusicfinder.localmusicfinderanalytics.errors;

import com.thelocalmusicfinder.localmusicfinderanalytics.services.LoggerService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
  private final LoggerService logger;

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception exception, HttpServletRequest request) {
    this.logError("Generic Exception: " + exception.getMessage(), request);

    ErrorResponse error = new ErrorResponse("INTERNAL_SERVER_ERROR", "Internal Server Error");
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private void logError(String customMessage, HttpServletRequest request) {
    String message = String.format(
            "{ path: \"%s\", method: \"%s\", remoteIp: \"%s\", customMessage: \"%s\" }",
            request.getRequestURI(),
            request.getMethod(),
            request.getRemoteAddr(),
            customMessage
    );
    logger.error(message);
  }
}
