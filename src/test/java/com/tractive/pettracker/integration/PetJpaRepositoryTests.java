package com.tractive.pettracker.integration;

import com.tractive.pettracker.data.jpa.PetEntity;
import com.tractive.pettracker.data.jpa.PetJpaRepository;
import com.tractive.pettracker.domain.PetType;
import com.tractive.pettracker.domain.TrackerType;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    void whenFindByIdNotFoundReturnsEmptyOptional() {
        Optional<PetEntity> found = repo.findById(-1L);
        assertThat(found).isEmpty();
    }

    @Test
    void whenFindAllReturnsAllSavedEntities() {
        repo.deleteAll();

        var catEntity = new PetEntity();
        catEntity.setPetType(PetType.CAT);
        catEntity.setTrackerType(TrackerType.SMALL);
        catEntity.setOwnerId(1);
        catEntity.setInZone(true);
        catEntity.setLostTracker(false);
        repo.save(catEntity);

        var dogEntity = new PetEntity();
        dogEntity.setPetType(PetType.DOG);
        dogEntity.setTrackerType(TrackerType.BIG);
        dogEntity.setOwnerId(2);
        dogEntity.setInZone(false);
        dogEntity.setLostTracker(null);
        repo.save(dogEntity);

        List<PetEntity> allPets = repo.findAll();

        assertThat(allPets).hasSize(2);
        assertThat(allPets).extracting(PetEntity::getPetType)
            .containsExactlyInAnyOrder(PetType.CAT, PetType.DOG);
    }

    @Test
    void whenCountOutsideZoneGroupedThenReturnsCorrectCounts() {
        repo.deleteAll();

        var catSmallInZone = new PetEntity();
        catSmallInZone.setPetType(PetType.CAT);
        catSmallInZone.setTrackerType(TrackerType.SMALL);
        catSmallInZone.setOwnerId(1);
        catSmallInZone.setInZone(false);
        catSmallInZone.setLostTracker(false);
        repo.save(catSmallInZone);

        var catSmallOutZone = new PetEntity();
        catSmallOutZone.setPetType(PetType.CAT);
        catSmallOutZone.setTrackerType(TrackerType.SMALL);
        catSmallOutZone.setOwnerId(2);
        catSmallOutZone.setInZone(false);
        catSmallOutZone.setLostTracker(true);
        repo.save(catSmallOutZone);

        var dogBigOutZone = new PetEntity();
        dogBigOutZone.setPetType(PetType.DOG);
        dogBigOutZone.setTrackerType(TrackerType.BIG);
        dogBigOutZone.setOwnerId(3);
        dogBigOutZone.setInZone(false);
        dogBigOutZone.setLostTracker(null);
        repo.save(dogBigOutZone);

        var dogSmallInZone = new PetEntity();
        dogSmallInZone.setPetType(PetType.DOG);
        dogSmallInZone.setTrackerType(TrackerType.SMALL);
        dogSmallInZone.setOwnerId(4);
        dogSmallInZone.setInZone(true);
        dogSmallInZone.setLostTracker(null);
        repo.save(dogSmallInZone);

        List<Object[]> counts = repo.countOutsideZoneGrouped();

        assertThat(counts).hasSize(2);

        boolean foundCatSmall = false;
        boolean foundDogBig = false;

        for (Object[] row : counts) {
            PetType petType = (PetType) row[0];
            TrackerType trackerType = (TrackerType) row[1];
            Long count = (Long) row[2];

            if (petType == PetType.CAT && trackerType == TrackerType.SMALL) {
                assertThat(count).isEqualTo(2L);
                foundCatSmall = true;
            } else if (petType == PetType.DOG && trackerType == TrackerType.BIG) {
                assertThat(count).isEqualTo(1L);
                foundDogBig = true;
            } else {
                throw new AssertionError("Unexpected grouping: " + petType + " " + trackerType);
            }
        }

        assertThat(foundCatSmall).isTrue();
        assertThat(foundDogBig).isTrue();
    }

}
