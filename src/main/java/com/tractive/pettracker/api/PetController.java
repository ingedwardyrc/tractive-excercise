package com.tractive.pettracker.api;

import com.tractive.pettracker.api.dto.PetRequestDTO;
import com.tractive.pettracker.api.dto.PetResponseDTO;
import com.tractive.pettracker.application.service.PetService;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class PetController implements PetControllerApi {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @Override
    public ResponseEntity<PetResponseDTO> create(PetRequestDTO petRequestDTO) {
        var created = petService.create(petRequestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.id())
            .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Override
    public ResponseEntity<PetResponseDTO> update(Long id, PetRequestDTO dto) {
        return ResponseEntity.ok(petService.update(id, dto));
    }

    @Override
    public ResponseEntity<PetResponseDTO> get(Long id) {
        return ResponseEntity.ok(petService.getById(id));
    }

    @Override
    public ResponseEntity<List<PetResponseDTO>> list() {
        return ResponseEntity.ok(petService.list());
    }
}
