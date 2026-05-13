package com.kanflow.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class StoryPointsValidator implements ConstraintValidator<StoryPoints, Integer> {

    private static final Set<Integer> ALLOWED = Set.of(1, 2, 3, 5, 8, 13);

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return ALLOWED.contains(value);
    }
}
