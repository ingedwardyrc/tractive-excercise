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

    @Autowired PetJpaRepository repo;

    @Test
    void save_and_find_roundTrip_cat_includingLost() {
        PetEntity e = new PetEntity();
        e.setPetType(PetType.CAT);
        e.setTrackerType(TrackerType.SMALL);
        e.setOwnerId(42);
        e.setInZone(true);
        e.setLostTracker(true);

        PetEntity saved = repo.save(e);
        assertThat(saved.getId()).isNotNull();

        PetEntity found = repo.findById(saved.getId()).orElseThrow();
        assertThat(found.getPetType()).isEqualTo(PetType.CAT);
        assertThat(found.getTrackerType()).isEqualTo(TrackerType.SMALL);
        assertThat(found.getOwnerId()).isEqualTo(42);
        assertThat(found.getInZone()).isTrue();
        assertThat(found.getLostTracker()).isTrue();
    }
}