package com.tractive.pettracker.data;

import com.tractive.pettracker.domain.Pet;
import java.util.List;
import java.util.Optional;

public interface PetRepository {
    Pet save(Pet pet);
    Optional<Pet> findById(Long id);
    List<Pet> findAll();
}
