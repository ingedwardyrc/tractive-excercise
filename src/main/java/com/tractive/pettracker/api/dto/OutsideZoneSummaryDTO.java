package com.tractive.pettracker.api.dto;

import com.tractive.pettracker.domain.PetType;
import com.tractive.pettracker.domain.TrackerType;

public record OutsideZoneSummaryDTO(
    PetType petType,
    TrackerType trackerType,
    Integer count
) {}
