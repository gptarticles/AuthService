package me.zedaster.authservice.controller;

import me.zedaster.authservice.dto.ErrorDto;
import me.zedaster.authservice.dto.ValidationErrorDto;
import me.zedaster.authservice.exception.AuthException;
import me.zedaster.authservice.exception.JwtException;
import me.zedaster.authservice.exception.ProfileException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception handlers for controllers.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link AuthException}
     * @param e The instance of the exception.
     * @return Json with error message from the exception.
     */
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorDto> handleAuthException(AuthException e) {
        return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link JwtException}
     * @param e The instance of the exception.
     * @return Json with error message from the exception.
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorDto> handleJwtException(JwtException e) {
        return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link ProfileException}
     * @param e The instance of the exception.
     * @return Json with error message from the exception.
     */
    @ExceptionHandler(ProfileException.class)
    public ResponseEntity<ErrorDto> handleProfileException(ProfileException e) {
        return new ResponseEntity<>(new ErrorDto(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles validation exceptions.
     * @param e The instance of the exception.
     * @return Json with error message from the exception.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorDto> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errorsByField = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errorsByField.put(error.getField(), error.getDefaultMessage())
        );
        ValidationErrorDto errorDto = new ValidationErrorDto(errorsByField);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }
}
