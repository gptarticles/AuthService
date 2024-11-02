package me.zedaster.authservice.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.*;

/**
 * Annotation for shallow password validating (good for logging in)
 * <br/><br/>
 * This pattern checks for:
 * <ul>
 *      <li>At least 8 characters</li>
 *      <li>No more than 128 characters</li>
 *      <li>Latin characters and arab digits are valid</li>
 *      <li>Other characters that are also valid: ~ ! ? @ # $ % ^ & * _ - + ( ) [ ] { } > < / \ | " ' . , : ;</li>
 * </ul>
 * If the password is not valid, message "Password is incorrect!" will be returned.
 * <br/>
 * Use {@link ExactPassword} if exact password validation is needed (for registration, changing password, etc.)
 */
@Pattern(regexp = "[a-zA-Z0-9~!?@#$%^&*_\\-+()\\[\\]{}></\\\\|\"'.,:;]{8,128}", message = "Password is incorrect!")
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Documented
public @interface ShallowPassword {
    String message() default "Incorrect password!";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
