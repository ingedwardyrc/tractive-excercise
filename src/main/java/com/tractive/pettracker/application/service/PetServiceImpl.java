package com.tractive.pettracker.application.service;

import com.tractive.pettracker.api.dto.PetRequestDTO;
import com.tractive.pettracker.api.dto.PetResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class PetServiceImpl implements PetService {

    @Override
    public PetResponseDTO create(PetRequestDTO dto) {
        return null;
    }
}
