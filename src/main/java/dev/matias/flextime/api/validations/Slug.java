package dev.matias.flextime.api.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SlugValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Slug {
    String message() default "Invalid slug";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
