package dev.matias.flextime.api.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SlugValidator implements ConstraintValidator<Slug, String> {

    private static final String SLUG_PATTERN = "^[a-z0-9]+(-[a-z0-9]+)*$";

    @Override
    public boolean isValid(String slug, ConstraintValidatorContext context){
        if (slug == null) return false;
        return slug.matches(SLUG_PATTERN);
    }

}
