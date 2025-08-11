package com.tractive.pettracker.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tractive.pettracker.api.dto.PetRequestDTO;
import com.tractive.pettracker.api.dto.PetResponseDTO;
import com.tractive.pettracker.application.service.PetServiceImpl;
import com.tractive.pettracker.data.PetRepository;
import com.tractive.pettracker.domain.Cat;
import com.tractive.pettracker.domain.Pet;
import com.tractive.pettracker.domain.PetType;
import com.tractive.pettracker.domain.TrackerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PetServiceImplTest {

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private PetServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void WhenCreatedCatShouldSavedDomainObjectWithAllFields() {
        PetRequestDTO petRequestDTO = new PetRequestDTO(PetType.CAT, TrackerType.SMALL, 1, true, true);

        when(petRepository.save(any(Pet.class))).thenAnswer(inv -> {
            Cat c = (Cat) inv.getArgument(0);
            return new Cat(100L, c.getTrackerType(), c.getOwnerId(), c.getInZone(), c.getLostTracker());
        });

        PetResponseDTO r = service.create(petRequestDTO);

        ArgumentCaptor<Pet> captor = ArgumentCaptor.forClass(Pet.class);
        verify(petRepository).save(captor.capture());

        var expectedMappedPet = new Cat(
            null,              // id is null before persist
            TrackerType.SMALL,
            1,
            true,
            true
        );

        assertThat(captor.getValue())
            .isInstanceOf(Cat.class);
        assertThat((Cat) captor.getValue())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(expectedMappedPet);

        assertThat(r.id()).isEqualTo(100L);
        assertThat(r.petType()).isEqualTo(PetType.CAT);
        assertThat(r.trackerType()).isEqualTo(TrackerType.SMALL);
        assertThat(r.ownerId()).isEqualTo(1);
        assertThat(r.inZone()).isTrue();
        assertThat(r.lostTracker()).isEqualTo(true);
    }

    @Test
    void WhenCreatedPetShouldSavedDomainObjectWithAllFieldsAndLostTrackerNull() {
        PetRequestDTO petRequestDTO = new PetRequestDTO(PetType.DOG, TrackerType.SMALL, 1, true, null);

        when(petRepository.save(any(Pet.class))).thenAnswer(inv -> {
            Pet c = (Pet) inv.getArgument(0);
            return new Pet(100L, PetType.DOG, c.getTrackerType(), c.getOwnerId(), c.getInZone());
        });

        PetResponseDTO r = service.create(petRequestDTO);

        ArgumentCaptor<Pet> captor = ArgumentCaptor.forClass(Pet.class);
        verify(petRepository).save(captor.capture());

        var expectedMappedPet = new Pet(
            null,
            PetType.DOG,
            TrackerType.SMALL,
            1,
            true
        );

        assertThat(captor.getValue())
            .isInstanceOf(Pet.class);
        assertThat((Pet) captor.getValue())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(expectedMappedPet);

        assertThat(r.id()).isEqualTo(100L);
        assertThat(r.petType()).isEqualTo(PetType.DOG);
        assertThat(r.trackerType()).isEqualTo(TrackerType.SMALL);
        assertThat(r.ownerId()).isEqualTo(1);
        assertThat(r.inZone()).isTrue();
        assertThat(r.lostTracker()).isEqualTo(null);
    }

    @Test
    void WhenCreateWillMapAllFieldsToPet() {
        PetRequestDTO petRequestDTO = new PetRequestDTO(PetType.CAT, TrackerType.SMALL, 1, false, null);
        ArgumentCaptor<Pet> captor = ArgumentCaptor.forClass(Pet.class);
        when(petRepository.save(captor.capture())).thenAnswer(inv -> {
            Pet p = captor.getValue();
            p.setId(10L);
            return p;
        });

        PetResponseDTO created = service.create(petRequestDTO);
        assertThat(created.id()).isEqualTo(10L);
        assertThat(created.petType()).isEqualTo(PetType.CAT);
        assertThat(created.trackerType()).isEqualTo(TrackerType.SMALL);
        assertThat(created.ownerId()).isEqualTo(1);
        assertThat(created.inZone()).isFalse();
    }
}
