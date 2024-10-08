package me.zedaster.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO that represents whether the operation was successful.
 */
@Data
@AllArgsConstructor
public class SuccessDto {
    /**
     * Whether the operation was successful.
     */
    private boolean success;
}
