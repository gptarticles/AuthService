package me.zedaster.authservice.controller;

import me.zedaster.authservice.dto.ErrorDto;
import me.zedaster.authservice.dto.ValidationErrorDto;
import me.zedaster.authservice.dto.auth.UserCredentialsDto;
import me.zedaster.authservice.exception.AuthException;
import me.zedaster.authservice.exception.JwtException;
import me.zedaster.authservice.exception.ProfileException;
import me.zedaster.authservice.exception.UserIdException;
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
     * Handles exceptions that can be shown with a simple error message.
     * @param exception The instance of the exception.
     * @return Json with error message from the exception.
     */
    @ExceptionHandler({AuthException.class, JwtException.class, ProfileException.class, UserIdException.class})
    public ResponseEntity<ErrorDto> handleSimpleException(Exception exception) {
        return new ResponseEntity<>(new ErrorDto(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles validation exceptions.
     * @param e The instance of the exception.
     * @return Json with error message from the exception.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidationException(MethodArgumentNotValidException e) {
        // If validation is failed in UserCredentialsDto, then standard exception will be thrown for security reasons
        if (e.getBindingResult().getTarget() instanceof UserCredentialsDto) {
            return handleSimpleException(AuthException.newInvalidCredentialsException());
        }

        Map<String, String> errorsByField = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errorsByField.put(error.getField(), error.getDefaultMessage())
        );
        ValidationErrorDto errorDto = new ValidationErrorDto(errorsByField);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }
}
