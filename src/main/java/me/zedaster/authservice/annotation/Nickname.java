package me.zedaster.authservice.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.*;

/**
 * Annotation for validating the nickname of the user.
 * <br/>
 * The nickname must start with a letter and contain only letters, digits, underscores and dots. The length must
 * be between 3 and 32 characters.
 * <br/>
 * If the nickname does not meet the requirements, message "Username does not meet the requirements!" will be returned.
 */
@Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9._]{2,31}$", message = "Username does not meet the requirements!")
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Documented
public @interface Nickname {
    String message() default "Invalid nickname!";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
}
