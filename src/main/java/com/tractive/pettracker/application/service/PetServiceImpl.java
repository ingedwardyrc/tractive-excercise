package com.tractive.pettracker.application.service;

import com.tractive.pettracker.api.dto.PetRequestDTO;
import com.tractive.pettracker.api.dto.PetResponseDTO;
import com.tractive.pettracker.data.PetRepository;
import com.tractive.pettracker.domain.Cat;
import com.tractive.pettracker.domain.Pet;
import com.tractive.pettracker.domain.PetType;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;

    public PetServiceImpl(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @Override
    public PetResponseDTO create(PetRequestDTO petRequestDTO) {
        Assert.notNull(petRequestDTO, "pet must not be null");
        Pet pet = toDomain(null, petRequestDTO);
        Pet saved = petRepository.save(pet);
        return toResponse(saved);
    }

    // Add validation to ensure that if is not cat and has lost tracker throws an error
    private Pet toDomain(Long id, PetRequestDTO petRequestDTO) {
        if (petRequestDTO.petType() == PetType.CAT) {
            Boolean lost = petRequestDTO.lostTracker() != null ? petRequestDTO.lostTracker() : Boolean.FALSE;
            return new Cat(id, petRequestDTO.trackerType(), petRequestDTO.ownerId(), petRequestDTO.inZone(), lost);
        } else {
            Pet pet = new Pet(id, petRequestDTO.petType(), petRequestDTO.trackerType(), petRequestDTO.ownerId(), petRequestDTO.inZone());
            return pet;
        }
    }

    private PetResponseDTO toResponse(Pet pet) {
        Boolean lost = null;
        if (pet instanceof Cat cat) lost = cat.getLostTracker();
        return new PetResponseDTO(
            pet.getId(),
            pet.getPetType(),
            pet.getTrackerType(),
            pet.getOwnerId(),
            pet.getInZone(),
            lost
        );
    }
}
