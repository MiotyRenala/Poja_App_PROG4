package com.example.demo.handler;

import com.example.demo.endpoint.rest.dto.ErrorResponse;
import com.example.demo.handler.exception.InvalidSubmissionException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({
    MissingServletRequestPartException.class,
    MissingServletRequestParameterException.class,
    InvalidSubmissionException.class
  })
  public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex) {
    return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
  }
}
