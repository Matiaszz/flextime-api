package dev.matias.flextime.api.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    private int minLength;
    private boolean requireSpecialChar;

    @Override
    public void initialize(Password constraintAnnotation) {
        this.minLength = constraintAnnotation.minLength();
        this.requireSpecialChar = constraintAnnotation.requireSpecialChar();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }

        if (password.length() < minLength) {
            return false;
        }

        if (requireSpecialChar && !containsSpecialCharacter(password)) {
            return false;
        }

        if (!containsUppercase(password)) {
            return false;
        }

        if (!containsLowercase(password)) {
            return false;
        }

        if (!containsNumber(password)) {
            return false;
        }

        if (containsWhitespace(password)) {
            return false;
        }

        if (containsNumbersOnly(password)){
            return false;
        }

        if (isOnlyLetters(password)){
            return false;
        }

        return true;
    }

    private boolean containsSpecialCharacter(String password) {
        return password.matches(".*[!@#$%^&*()\\-+=\\[\\]{}|;:'\",.<>/?].*");
    }

    private boolean containsUppercase(String password) {
        return password.matches(".*[A-Z].*");
    }

    private boolean containsLowercase(String password) {
        return password.matches(".*[a-z].*");
    }

    private boolean containsNumber(String password) {
        return password.matches(".*\\d.*");
    }

    private boolean containsWhitespace(String password) {
        return password.matches(".*\\s.*");
    }

    private boolean containsNumbersOnly(String password){
        return password.matches("^[0-9]+$");
    }

    private boolean isOnlyLetters(String password) {
        return password.matches("^[a-zA-Z]+$");
    }
}
