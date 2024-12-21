package com.practice.slideshow.exception;

import com.practice.slideshow.dto.ErrorResponse;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.validation.FieldError;


/**
 * Global exception handler to manage application-wide errors.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /**
   * Handles ResourceNotFoundException.
   *
   * @param ex The exception thrown.
   * @return The error response with details.
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleNotFound(ResourceNotFoundException ex) {
    log.error("Resource not found: {}", ex.getMessage());
    return ErrorResponse.builder().errorCode("NOT_FOUND").message(ex.getMessage()).build();
  }

  /**
   * Handles InvalidRequestException.
   *
   * @param ex The exception thrown.
   * @return The error response with details.
   */
  @ExceptionHandler(InvalidRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleInvalidRequest(InvalidRequestException ex) {
    log.error("Invalid request: {}", ex.getMessage());
    return ErrorResponse.builder().errorCode("BAD_REQUEST").message(ex.getMessage()).build();
  }

  /**
   * Handles validation errors (e.g., invalid method arguments).
   *
   * @param ex The exception thrown.
   * @return The error response with details.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleValidationError(MethodArgumentNotValidException ex) {
    String msg = ex.getBindingResult().getFieldErrors().stream()
        .map(FieldError::getDefaultMessage)
        .collect(Collectors.joining(", "));
    log.error("Validation error: {}", msg);
    return ErrorResponse.builder().errorCode("VALIDATION_ERROR").message(msg).build();
  }

  /**
   * Handles JSON parsing errors.
   *
   * @param ex The exception thrown.
   * @return The error response with details.
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleJsonParseError(HttpMessageNotReadableException ex) {
    log.error("Malformed JSON request: {}", ex.getMessage());
    return ErrorResponse.builder().errorCode("MALFORMED_JSON")
        .message("Unable to read query body").build();
  }

  /**
   * Handles all other exceptions.
   *
   * @param ex The exception thrown.
   * @return The error response with details.
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleOtherExceptions(Exception ex) {
    log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
    return ErrorResponse.builder().errorCode("INTERNAL_ERROR")
        .message("An unexpected error has occurred").build();
  }
}
