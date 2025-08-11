package com.tractive.pettracker.api;

import com.tractive.pettracker.api.dto.PetRequestDTO;
import com.tractive.pettracker.api.dto.PetResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PetController implements PetControllerApi {

    @Override
    public ResponseEntity<PetResponseDTO> create(PetRequestDTO dto) {
        return null;
    }
}
