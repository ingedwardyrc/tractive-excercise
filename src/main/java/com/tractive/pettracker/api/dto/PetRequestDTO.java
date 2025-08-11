package com.tractive.pettracker.api.dto;

import com.tractive.pettracker.api.dto.validation.ValidPetTracker;
import com.tractive.pettracker.domain.PetType;
import com.tractive.pettracker.domain.TrackerType;
import jakarta.validation.constraints.NotNull;

@ValidPetTracker
public record PetRequestDTO(
    @NotNull PetType petType,
    @NotNull TrackerType trackerType,
    @NotNull Integer ownerId,
    @NotNull Boolean inZone,
    Boolean lostTracker
) {}
