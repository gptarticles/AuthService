package me.zedaster.authservice.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.*;

/**
 * Annotation for exact password validating (for registration, changing password, etc.)
 * <br/><br/>
 * This pattern checks for:
 * <ul>
 *     <li>At least 8 characters</li>
 *     <li>No more than 128 characters</li>
 *     <li>At least one uppercase and one lowercase letter</li>
 *     <li>Latin letters only</li>
 *     <li>At least one numeral</li>
 *     <li>Arabic numerals only</li>
 *     <li>No spaces</li>
 *     <li>Other characters that are also valid: ~ ! ? @ # $ % ^ & * _ - + ( ) [ ] { } > < / \ | " ' . , : ;</li>
 * </ul>
 * If the password is not valid, message "Password does not meet the requirements!" will be returned.
 * <br/>
 * If shallow password validation is enough, you can {@link ShallowPassword} for better performance.
 */
@Pattern(regexp = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[a-zA-Z0-9~!?@#$%^&*_\\-+()\\[\\]{}></\\\\|\"'.,:;]{8,128}",
        message = "Password does not meet the requirements!")
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Documented
public @interface ExactPassword {
    String message() default "Incorrect password!";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
