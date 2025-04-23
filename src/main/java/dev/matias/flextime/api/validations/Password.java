package dev.matias.flextime.api.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default "Invalid password";

    int minLength() default 8;
    boolean requireSpecialChar() default true;

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
