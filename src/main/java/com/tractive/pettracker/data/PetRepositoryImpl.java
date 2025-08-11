package com.tractive.pettracker.data;

import com.tractive.pettracker.data.jpa.PetEntity;
import com.tractive.pettracker.data.jpa.PetJpaRepository;
import com.tractive.pettracker.domain.Cat;
import com.tractive.pettracker.domain.Pet;
import com.tractive.pettracker.domain.PetType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class PetRepositoryImpl implements PetRepository {

    private final PetJpaRepository jpa;

    public PetRepositoryImpl(PetJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Pet save(Pet pet) {
        PetEntity entity = toEntity(pet);
        entity.setId(null);
        PetEntity saved = jpa.save(entity);
        return toDomain(saved);
    }

    @Override
    public Pet update(Pet pet) {
        if (pet.getId() == null) throw new IllegalArgumentException("id required for update");
        PetEntity entity = toEntity(pet);
        PetEntity saved = jpa.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Pet> findById(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<Pet> findAll() {
        List<Pet> out = new ArrayList<>();
        for (PetEntity e : jpa.findAll()) out.add(toDomain(e));
        return out;
    }

    private PetEntity toEntity(Pet pet) {
        PetEntity e = new PetEntity();
        e.setId(pet.getId());
        e.setPetType(pet.getPetType());
        e.setTrackerType(pet.getTrackerType());
        e.setOwnerId(pet.getOwnerId());
        e.setInZone(pet.getInZone());
        if (pet instanceof Cat c) e.setLostTracker(c.getLostTracker());
        else e.setLostTracker(null);
        return e;
    }

    private Pet toDomain(PetEntity e) {
        if (e.getPetType() == PetType.CAT) {
            return new Cat(e.getId(), e.getTrackerType(), e.getOwnerId(), e.getInZone(), e.getLostTracker() != null ? e.getLostTracker() : Boolean.FALSE);
        } else {
            return new Pet(e.getId(), e.getPetType(), e.getTrackerType(), e.getOwnerId(), e.getInZone());
        }
    }
}
