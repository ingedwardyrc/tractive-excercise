package com.tractive.pettracker.application.service;

import com.tractive.pettracker.api.dto.PetRequestDTO;
import com.tractive.pettracker.api.dto.PetResponseDTO;

public interface PetService {
    PetResponseDTO create(PetRequestDTO dto);
}
