package me.zedaster.authservice.dto;

import lombok.Getter;

import java.util.Map;

/**
 * DTO for errors that occurred during validation.
 */
@Getter
public class ValidationErrorDto extends ErrorDto {
    public ValidationErrorDto(Map<String, String> errorsByField) {
        super("Error validating the request data!");
        this.errorsByField = errorsByField;
    }

    /**
     * All validation errors by field.
     */
    private final Map<String, String> errorsByField;
}
