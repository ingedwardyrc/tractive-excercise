package com.tractive.pettracker.data;

import com.tractive.pettracker.domain.Pet;

public interface PetRepository {
    Pet save(Pet pet);
}
