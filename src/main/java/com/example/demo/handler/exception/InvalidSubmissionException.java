package com.example.demo.handler.exception;

public class InvalidSubmissionException extends RuntimeException {
  public InvalidSubmissionException(String message) {
    super(message);
  }
}
