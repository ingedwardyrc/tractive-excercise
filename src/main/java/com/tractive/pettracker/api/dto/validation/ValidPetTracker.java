package com.tractive.pettracker.api.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPetTrackerValidator.class)
public @interface ValidPetTracker {
    String message() default "Invalid tracker type for pet type";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
