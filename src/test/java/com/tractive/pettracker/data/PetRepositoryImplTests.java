package com.tractive.pettracker.data;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.tractive.pettracker.data.jpa.PetEntity;
import com.tractive.pettracker.data.jpa.PetJpaRepository;
import com.tractive.pettracker.domain.Cat;
import com.tractive.pettracker.domain.Pet;
import com.tractive.pettracker.domain.PetType;
import com.tractive.pettracker.domain.TrackerType;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SpringExtension.class)
class PetRepositoryImplTests {

    @TestConfiguration
    static class TestConfig {
        @Bean
        PetRepositoryImpl petRepositoryImpl(PetJpaRepository jpa) {
            return new PetRepositoryImpl(jpa);
        }
    }

    @MockitoBean
    private PetJpaRepository jpa;

    @Autowired
    private PetRepositoryImpl repo;

    @Test
    void saveShouldSetIdToNullBeforeSavingAndReturnDomain() {
        var pet = new Pet(null, PetType.DOG, TrackerType.SMALL, 10, true);

        var savedPetEntity = new PetEntity();
        savedPetEntity.setId(1L);
        savedPetEntity.setPetType(PetType.DOG);
        savedPetEntity.setTrackerType(TrackerType.SMALL);
        savedPetEntity.setOwnerId(10);
        savedPetEntity.setInZone(true);
        savedPetEntity.setLostTracker(null);

        when(jpa.save(any(PetEntity.class))).thenReturn(savedPetEntity);

        var result = repo.save(pet);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPetType()).isEqualTo(PetType.DOG);
        assertThat(result.getTrackerType()).isEqualTo(TrackerType.SMALL);
        assertThat(result.getOwnerId()).isEqualTo(10);
        assertThat(result.getInZone()).isTrue();

        var captor = ArgumentCaptor.forClass(PetEntity.class);
        verify(jpa).save(captor.capture());
        assertThat(captor.getValue().getId()).isNull();
    }

    @Test
    void updateShouldThrowIfIdIsNull() {
        var petWithoutId = new Pet(null, PetType.DOG, TrackerType.SMALL, 1, true);

        assertThatThrownBy(() -> repo.update(petWithoutId))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("id required for update");
    }

    @Test
    void updateShouldSaveAndReturnUpdatedDomain() {
        var pet = new Pet(42L, PetType.DOG, TrackerType.MEDIUM, 20, false);

        var savedPetEntity = new PetEntity();
        savedPetEntity.setId(42L);
        savedPetEntity.setPetType(PetType.DOG);
        savedPetEntity.setTrackerType(TrackerType.MEDIUM);
        savedPetEntity.setOwnerId(20);
        savedPetEntity.setInZone(false);

        when(jpa.save(any(PetEntity.class))).thenReturn(savedPetEntity);

        var updated = repo.update(pet);

        assertThat(updated.getId()).isEqualTo(42L);
        assertThat(updated.getPetType()).isEqualTo(PetType.DOG);
        assertThat(updated.getTrackerType()).isEqualTo(TrackerType.MEDIUM);
        assertThat(updated.getOwnerId()).isEqualTo(20);
        assertThat(updated.getInZone()).isFalse();

        var captor = ArgumentCaptor.forClass(PetEntity.class);
        verify(jpa).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(42L);
    }

    @Test
    void findByIdShouldReturnDomainIfFound() {
        var petEntity = new PetEntity();
        petEntity.setId(7L);
        petEntity.setPetType(PetType.DOG);
        petEntity.setTrackerType(TrackerType.BIG);
        petEntity.setOwnerId(5);
        petEntity.setInZone(true);
        petEntity.setLostTracker(null);

        when(jpa.findById(7L)).thenReturn(Optional.of(petEntity));

        var result = repo.findById(7L);

        assertThat(result).isPresent();
        var pet = result.get();
        assertThat(pet.getId()).isEqualTo(7L);
        assertThat(pet.getPetType()).isEqualTo(PetType.DOG);
        assertThat(pet.getTrackerType()).isEqualTo(TrackerType.BIG);
        assertThat(pet.getOwnerId()).isEqualTo(5);
        assertThat(pet.getInZone()).isTrue();
    }

    @Test
    void findByIdShouldReturnEmptyIfNotFound() {
        when(jpa.findById(99L)).thenReturn(Optional.empty());

        var result = repo.findById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void findAllShouldReturnAllMappedToDomain() {
        var catPetEntity = new PetEntity();
        catPetEntity.setId(1L);
        catPetEntity.setPetType(PetType.CAT);
        catPetEntity.setTrackerType(TrackerType.SMALL);
        catPetEntity.setOwnerId(10);
        catPetEntity.setInZone(false);
        catPetEntity.setLostTracker(true);

        var dogPetEntity = new PetEntity();
        dogPetEntity.setId(2L);
        dogPetEntity.setPetType(PetType.DOG);
        dogPetEntity.setTrackerType(TrackerType.BIG);
        dogPetEntity.setOwnerId(20);
        dogPetEntity.setInZone(true);
        dogPetEntity.setLostTracker(null);

        when(jpa.findAll()).thenReturn(List.of(catPetEntity, dogPetEntity));

        var allPets = repo.findAll();

        assertThat(allPets).hasSize(2);

        var first = allPets.stream().filter(p -> p.getId() == 1L).findFirst().orElse(null);
        assertThat(first).isInstanceOf(Cat.class);
        assertThat(((Cat) first).getLostTracker()).isTrue();

        var second = allPets.stream().filter(p -> p.getId() == 2L).findFirst().orElse(null);
        assertThat(second).isNotInstanceOf(Cat.class);
        assertThat(second.getPetType()).isEqualTo(PetType.DOG);
    }

    @Test
    void countOutsideZoneGroupedShouldReturnMappedResults() {
        Object[] row1 = new Object[] { PetType.CAT, TrackerType.SMALL, 3L };
        Object[] row2 = new Object[] { PetType.DOG, TrackerType.BIG, 5L };

        when(jpa.countOutsideZoneGrouped()).thenReturn(List.of(row1, row2));

        var results = repo.countOutsideZoneGrouped();

        assertThat(results).hasSize(2);

        var catCount = results.stream()
            .filter(c -> c.getPetType() == PetType.CAT && c.getTrackerType() == TrackerType.SMALL)
            .findFirst().orElse(null);
        assertThat(catCount).isNotNull();
        assertThat(catCount.getCount()).isEqualTo(3);

        var dogCount = results.stream()
            .filter(c -> c.getPetType() == PetType.DOG && c.getTrackerType() == TrackerType.BIG)
            .findFirst().orElse(null);
        assertThat(dogCount).isNotNull();
        assertThat(dogCount.getCount()).isEqualTo(5);
    }
}
