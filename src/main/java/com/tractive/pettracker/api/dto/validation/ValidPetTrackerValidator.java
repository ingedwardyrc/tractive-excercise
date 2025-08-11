package com.tractive.pettracker.api.dto.validation;


import com.tractive.pettracker.api.dto.PetRequestDTO;
import com.tractive.pettracker.domain.PetType;
import com.tractive.pettracker.domain.TrackerType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidPetTrackerValidator implements ConstraintValidator<ValidPetTracker, PetRequestDTO> {
    @Override
    public boolean isValid(PetRequestDTO dto, ConstraintValidatorContext context) {
        if (dto == null) return true;
        if (dto.petType() == PetType.CAT && dto.trackerType() == TrackerType.MEDIUM) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Cats can only have SMALL or BIG trackers")
                   .addPropertyNode("trackerType")
                   .addConstraintViolation();
            return false;
        }
        return true;
    }
}
