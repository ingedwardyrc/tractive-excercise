package com.tractive.pettracker.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tractive.pettracker.api.dto.PetRequestDTO;
import com.tractive.pettracker.application.exceptions.NotFoundException;
import com.tractive.pettracker.application.service.PetServiceImpl;
import com.tractive.pettracker.data.PetRepository;
import com.tractive.pettracker.domain.Cat;
import com.tractive.pettracker.domain.Pet;
import com.tractive.pettracker.domain.PetType;
import com.tractive.pettracker.domain.TrackerType;
import java.util.List;
import java.util.Optional;
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
    private PetServiceImpl petService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenCreateCatThenSavedDomainObjectMatchesAndResponseMapped() {
        var requestDto = new PetRequestDTO(PetType.CAT, TrackerType.SMALL, 1, true, true);

        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> {
            var savedCat = (Cat) invocation.getArgument(0);
            return new Cat(100L, savedCat.getTrackerType(), savedCat.getOwnerId(), savedCat.getInZone(), savedCat.getLostTracker());
        });

        var petDto = petService.create(requestDto);

        var petCaptor = ArgumentCaptor.forClass(Pet.class);
        verify(petRepository).save(petCaptor.capture());

        var expectedCat = new Cat(null, TrackerType.SMALL, 1, true, true);

        assertThat(petCaptor.getValue()).isInstanceOf(Cat.class);
        assertThat((Cat) petCaptor.getValue())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(expectedCat);

        assertThat(petDto.id()).isEqualTo(100L);
        assertThat(petDto.petType()).isEqualTo(PetType.CAT);
        assertThat(petDto.trackerType()).isEqualTo(TrackerType.SMALL);
        assertThat(petDto.ownerId()).isEqualTo(1);
        assertThat(petDto.inZone()).isTrue();
        assertThat(petDto.lostTracker()).isEqualTo(true);
    }

    @Test
    void whenCreateDogThenSavedDomainObjectMatchesAndResponseMappedWithLostNull() {
        var requestDto = new PetRequestDTO(PetType.DOG, TrackerType.SMALL, 1, true, null);

        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> {
            var savedPet = (Pet) invocation.getArgument(0);
            return new Pet(100L, PetType.DOG, savedPet.getTrackerType(), savedPet.getOwnerId(), savedPet.getInZone());
        });

        var petDto = petService.create(requestDto);

        var petCaptor = ArgumentCaptor.forClass(Pet.class);
        verify(petRepository).save(petCaptor.capture());

        var expectedPet = new Pet(null, PetType.DOG, TrackerType.SMALL, 1, true);

        assertThat(petCaptor.getValue()).isInstanceOf(Pet.class);
        assertThat((Pet) petCaptor.getValue())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(expectedPet);

        assertThat(petDto.id()).isEqualTo(100L);
        assertThat(petDto.petType()).isEqualTo(PetType.DOG);
        assertThat(petDto.trackerType()).isEqualTo(TrackerType.SMALL);
        assertThat(petDto.ownerId()).isEqualTo(1);
        assertThat(petDto.inZone()).isTrue();
        assertThat(petDto.lostTracker()).isEqualTo(null);
    }

    @Test
    void whenCreatePetThenMapsAllFields() {
        var requestDto = new PetRequestDTO(PetType.CAT, TrackerType.SMALL, 1, false, null);
        var petCaptor = ArgumentCaptor.forClass(Pet.class);

        when(petRepository.save(petCaptor.capture())).thenAnswer(invocation -> {
            var capturedPet = petCaptor.getValue();
            capturedPet.setId(10L);
            return capturedPet;
        });

        var createdPet = petService.create(requestDto);
        assertThat(createdPet.id()).isEqualTo(10L);
        assertThat(createdPet.petType()).isEqualTo(PetType.CAT);
        assertThat(createdPet.trackerType()).isEqualTo(TrackerType.SMALL);
        assertThat(createdPet.ownerId()).isEqualTo(1);
        assertThat(createdPet.inZone()).isFalse();
    }

    @Test
    void whenCreateWithNullRequestThenThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> petService.create(null));
    }

    @Test
    void whenCreateCatWithNullLostThenDefaultsLostToFalse() {
        var requestDto = new PetRequestDTO(PetType.CAT, TrackerType.BIG, 77, true, null);

        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> {
            var savedCat = (Cat) invocation.getArgument(0);
            assertThat(savedCat.getLostTracker()).isFalse();
            return new Cat(5L, savedCat.getTrackerType(), savedCat.getOwnerId(), savedCat.getInZone(), savedCat.getLostTracker());
        });

        var petDto = petService.create(requestDto);

        assertThat(petDto.id()).isEqualTo(5L);
        assertThat(petDto.petType()).isEqualTo(PetType.CAT);
        assertThat(petDto.trackerType()).isEqualTo(TrackerType.BIG);
        assertThat(petDto.ownerId()).isEqualTo(77);
        assertThat(petDto.inZone()).isTrue();
        assertThat(petDto.lostTracker()).isEqualTo(false);
    }

    @Test
    void whenGetByIdCatThenReturnsMappedDtoIncludingLost() {
        when(petRepository.findById(42L)).thenReturn(
            Optional.of(new Cat(42L, TrackerType.SMALL, 123, true, true))
        );

        var petDto = petService.getById(42L);

        assertThat(petDto.id()).isEqualTo(42L);
        assertThat(petDto.petType()).isEqualTo(PetType.CAT);
        assertThat(petDto.trackerType()).isEqualTo(TrackerType.SMALL);
        assertThat(petDto.ownerId()).isEqualTo(123);
        assertThat(petDto.inZone()).isTrue();
        assertThat(petDto.lostTracker()).isEqualTo(true);
    }

    @Test
    void whenGetByIdDogThenReturnsMappedDtoWithLostNull() {
        when(petRepository.findById(7L)).thenReturn(
            Optional.of(new Pet(7L, PetType.DOG, TrackerType.MEDIUM, 55, false))
        );

        var petDto = petService.getById(7L);

        assertThat(petDto.id()).isEqualTo(7L);
        assertThat(petDto.petType()).isEqualTo(PetType.DOG);
        assertThat(petDto.trackerType()).isEqualTo(TrackerType.MEDIUM);
        assertThat(petDto.ownerId()).isEqualTo(55);
        assertThat(petDto.inZone()).isEqualTo(false);
        assertThat(petDto.lostTracker()).isEqualTo(null);
    }

    @Test
    void whenGetByIdMissingThenThrowsNotFound() {
        when(petRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> petService.getById(999L));
    }

    @Test
    void whenListEmptyThenReturnEmptyList() {
        when(petRepository.findAll()).thenReturn(List.of());

        var result = petService.list();

        assertThat(result).isNotNull();
        assertThat(result.isEmpty()).isTrue();

    }

    @Test
    void whenListHasPetsThenReturnMappedDtos() {
        var cat = new Cat(1L, TrackerType.BIG, 10, true, true);
        var dog = new Pet(2L, PetType.DOG, TrackerType.SMALL, 20, false);

        when(petRepository.findAll()).thenReturn(List.of(cat, dog));

        var result = petService.list();

        assertThat(result.size()).isEqualTo(2);

        var catDto = result.get(0);
        assertThat(catDto.id()).isEqualTo(1L);
        assertThat(catDto.petType()).isEqualTo(PetType.CAT);
        assertThat(catDto.trackerType()).isEqualTo(TrackerType.BIG);
        assertThat(catDto.ownerId()).isEqualTo(10);
        assertThat(catDto.inZone()).isTrue();
        assertThat(catDto.lostTracker()).isTrue();

        var dogDto = result.get(1);
        assertThat(dogDto.id()).isEqualTo(2L);
        assertThat(dogDto.petType()).isEqualTo(PetType.DOG);
        assertThat(dogDto.trackerType()).isEqualTo(TrackerType.SMALL);
        assertThat(dogDto.ownerId()).isEqualTo(20);
        assertThat(dogDto.inZone()).isFalse();
        assertThat(dogDto.lostTracker()).isNull();
    }
}
