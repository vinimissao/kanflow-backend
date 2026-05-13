package com.kanflow.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = StoryPointsValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface StoryPoints {

    String message() default "Pontos deve ser um valor Fibonacci: 1, 2, 3, 5, 8 ou 13";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
