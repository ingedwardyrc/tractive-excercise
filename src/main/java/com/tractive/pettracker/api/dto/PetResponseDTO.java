package com.tractive.pettracker.api.dto;


import com.tractive.pettracker.domain.PetType;
import com.tractive.pettracker.domain.TrackerType;

public record PetResponseDTO(
    Long id,
    PetType petType,
    TrackerType trackerType,
    Integer ownerId,
    Boolean inZone,
    Boolean lostTracker
) {}
