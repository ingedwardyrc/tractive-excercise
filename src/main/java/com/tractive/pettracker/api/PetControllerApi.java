package com.tractive.pettracker.api;

import com.tractive.pettracker.api.dto.PetRequestDTO;
import com.tractive.pettracker.api.dto.PetResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/pets")
public interface PetControllerApi {
    @PostMapping
    ResponseEntity<PetResponseDTO> create(@Valid @RequestBody PetRequestDTO dto);

}
