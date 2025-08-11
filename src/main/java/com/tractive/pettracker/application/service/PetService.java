package com.tractive.pettracker.application.service;

import com.tractive.pettracker.api.dto.PetRequestDTO;
import com.tractive.pettracker.api.dto.PetResponseDTO;
import java.util.List;

public interface PetService {
    PetResponseDTO create(PetRequestDTO dto);
    PetResponseDTO update(Long id, PetRequestDTO dto);
    PetResponseDTO getById(Long id);
    List<PetResponseDTO> list();
}
