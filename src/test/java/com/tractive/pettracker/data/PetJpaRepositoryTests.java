package com.tractive.pettracker.data.jpa;

import com.tractive.pettracker.domain.PetType;
import com.tractive.pettracker.domain.TrackerType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PetJpaRepositoryTests {

    @Autowired
    PetJpaRepository repo;

    @Test
    void whenSaveCatThenRoundTripsIncludingLost() {
        var petEntity = new PetEntity();
        petEntity.setPetType(PetType.CAT);
        petEntity.setTrackerType(TrackerType.SMALL);
        petEntity.setOwnerId(42);
        petEntity.setInZone(true);
        petEntity.setLostTracker(true);

        var savedPetEntity = repo.save(petEntity);
        assertThat(savedPetEntity.getId()).isNotNull();

        var foundPetEntity = repo.findById(savedPetEntity.getId()).orElseThrow();
        assertThat(foundPetEntity.getPetType()).isEqualTo(PetType.CAT);
        assertThat(foundPetEntity.getTrackerType()).isEqualTo(TrackerType.SMALL);
        assertThat(foundPetEntity.getOwnerId()).isEqualTo(42);
        assertThat(foundPetEntity.getInZone()).isTrue();
        assertThat(foundPetEntity.getLostTracker()).isTrue();
    }

    @Test
    void whenSaveDogThenLostIsNullAndRoundTrips() {
        var petEntity = new PetEntity();
        petEntity.setPetType(PetType.DOG);
        petEntity.setTrackerType(TrackerType.MEDIUM);
        petEntity.setOwnerId(7);
        petEntity.setInZone(false);
        petEntity.setLostTracker(null);

        var savedPetEntity = repo.save(petEntity);
        assertThat(savedPetEntity.getId()).isNotNull();

        var foundPetEntity = repo.findById(savedPetEntity.getId()).orElseThrow();
        assertThat(foundPetEntity.getPetType()).isEqualTo(PetType.DOG);
        assertThat(foundPetEntity.getTrackerType()).isEqualTo(TrackerType.MEDIUM);
        assertThat(foundPetEntity.getOwnerId()).isEqualTo(7);
        assertThat(foundPetEntity.getInZone()).isFalse();
        assertThat(foundPetEntity.getLostTracker()).isNull();
    }
}
