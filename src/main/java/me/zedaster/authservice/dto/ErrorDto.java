package me.zedaster.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO that represents an error message.
 */
@Data
@AllArgsConstructor
public class ErrorDto {
    /**
     * Error message.
     */
    private final String message;
}
