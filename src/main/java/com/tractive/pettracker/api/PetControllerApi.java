package com.tractive.pettracker.api;

import com.tractive.pettracker.api.dto.PetRequestDTO;
import com.tractive.pettracker.api.dto.PetResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/pets")
public interface PetControllerApi {
    @PostMapping
    ResponseEntity<PetResponseDTO> create(@Valid @RequestBody PetRequestDTO dto);

    @GetMapping("/{id}")
    ResponseEntity<PetResponseDTO> get(@PathVariable Long id);
}
