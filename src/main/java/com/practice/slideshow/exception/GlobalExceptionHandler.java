package com.practice.slideshow.exception;

import com.practice.slideshow.dto.ErrorResponse;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public ErrorResponse handleNotFound(ResourceNotFoundException ex) {
    return ErrorResponse.builder().errorCode("NOT_FOUND").message(ex.getMessage()).build();
  }

  @ExceptionHandler(InvalidRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorResponse handleInvalidRequest(InvalidRequestException ex) {
    return ErrorResponse.builder().errorCode("BAD_REQUEST").message(ex.getMessage()).build();
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorResponse handleValidationError(MethodArgumentNotValidException ex) {
    String msg = ex.getBindingResult().getFieldErrors().stream()
        .map(e -> e.getField() + " " + e.getDefaultMessage())
        .collect(Collectors.joining(", "));
    return ErrorResponse.builder().errorCode("VALIDATION_ERROR").message(msg).build();
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorResponse handleJsonParseError(HttpMessageNotReadableException ex) {
    return ErrorResponse.builder().errorCode("MALFORMED_JSON")
        .message("Unable to read query body").build();
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public ErrorResponse handleOtherExceptions(Exception ex) {
    return ErrorResponse.builder().errorCode("INTERNAL_ERROR")
        .message("An unexpected error has occurred").build();
  }
}
