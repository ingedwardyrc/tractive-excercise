package com.tractive.pettracker.api;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tractive.pettracker.api.dto.OutsideZoneSummaryDTO;
import com.tractive.pettracker.api.dto.PetRequestDTO;
import com.tractive.pettracker.api.dto.PetResponseDTO;
import com.tractive.pettracker.application.exceptions.NotFoundException;
import com.tractive.pettracker.application.service.PetService;
import com.tractive.pettracker.domain.PetType;
import com.tractive.pettracker.domain.TrackerType;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PetController.class)
class PetControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private PetService petService;

    @Test
    void whenCreatePetReturns201AndLocation() throws Exception {
        var petResponseDTO = new PetResponseDTO(1L, PetType.CAT, TrackerType.SMALL, 123, false, false);
        var petRequestDTO = new PetRequestDTO(PetType.CAT, TrackerType.SMALL, 123, false, false);

        when(petService.create(petRequestDTO))
            .thenReturn(petResponseDTO);

        mvc.perform(post("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"petType":"CAT","trackerType":"SMALL","ownerId":123,"inZone":false,"lostTracker":false}
                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/pets/1")))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void whenGetPetThenReturns200AndPet() throws Exception {
        var response = new PetResponseDTO(42L, PetType.CAT, TrackerType.BIG, 55, true, true);
        when(petService.getById(42L)).thenReturn(response);

        mvc.perform(get("/api/pets/{id}", 42L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(42))
            .andExpect(jsonPath("$.petType").value("CAT"))
            .andExpect(jsonPath("$.trackerType").value("BIG"))
            .andExpect(jsonPath("$.lostTracker").value(true));
    }


    @Test
    void whenGetUnknownIdThenReturns404WithErrorAndMessage() throws Exception {
        when(petService.getById(5L)).thenThrow(new NotFoundException("Pet 5 not found"));

        mvc.perform(get("/api/pets/{id}", 5L).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("Pet 5 not found"));
    }

    @Test
    void whenCreateCatWithInvalidTrackerThenReturns400WithErrorAndMessage() throws Exception {
        mvc.perform(post("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("""
                    {"petType":"CAT","trackerType":"MEDIUM","ownerId":123,"inZone":false,"lostTracker":false}
                """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"))
            .andExpect(jsonPath("$.details.trackerType").value("Cats can only have SMALL or BIG trackers"));
    }

    @Test
    void whenListPetsThenReturns200AndListOfPets() throws Exception {
        var pet1 = new PetResponseDTO(1L, PetType.CAT, TrackerType.SMALL, 123, true, false);
        var pet2 = new PetResponseDTO(2L, PetType.DOG, TrackerType.BIG, 456, false, true);

        when(petService.list()).thenReturn(List.of(pet1, pet2));

        mvc.perform(get("/api/pets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].petType").value("CAT"))
            .andExpect(jsonPath("$[0].trackerType").value("SMALL"))
            .andExpect(jsonPath("$[0].ownerId").value(123))
            .andExpect(jsonPath("$[0].inZone").value(true))
            .andExpect(jsonPath("$[0].lostTracker").value(false))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].petType").value("DOG"))
            .andExpect(jsonPath("$[1].trackerType").value("BIG"))
            .andExpect(jsonPath("$[1].ownerId").value(456))
            .andExpect(jsonPath("$[1].inZone").value(false))
            .andExpect(jsonPath("$[1].lostTracker").value(true));
    }

    @Test
    void whenListPetsAndEmptyThenReturns200AndEmptyArray() throws Exception {
        when(petService.list()).thenReturn(List.of());

        mvc.perform(get("/api/pets").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void whenUpdatePetReturns200AndUpdatedPet() throws Exception {
        var petRequestDTO = new PetRequestDTO(PetType.DOG, TrackerType.MEDIUM, 456, true, null);
        var petResponseDTO = new PetResponseDTO(10L, PetType.DOG, TrackerType.MEDIUM, 456, true, null);

        when(petService.update(10L, petRequestDTO)).thenReturn(petResponseDTO);

        mvc.perform(put("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"petType":"DOG","trackerType":"MEDIUM","ownerId":456,"inZone":true,"lostTracker":null}
                """))
            .andExpect(status().isMethodNotAllowed());

        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/pets/{id}", 10L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"petType":"DOG","trackerType":"MEDIUM","ownerId":456,"inZone":true,"lostTracker":null}
                """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.petType").value("DOG"))
            .andExpect(jsonPath("$.trackerType").value("MEDIUM"))
            .andExpect(jsonPath("$.ownerId").value(456))
            .andExpect(jsonPath("$.inZone").value(true))
            .andExpect(jsonPath("$.lostTracker").doesNotExist());
    }

    @Test
    void whenUpdateUnknownPetReturns404() throws Exception {
        var petRequestDTO = new PetRequestDTO(PetType.DOG, TrackerType.MEDIUM, 456, true, null);

        when(petService.update(999L, petRequestDTO))
            .thenThrow(new NotFoundException("Pet 999 not found"));

        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/pets/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"petType":"DOG","trackerType":"MEDIUM","ownerId":456,"inZone":true,"lostTracker":null}
                """))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("Pet 999 not found"));
    }

    @Test
    void whenCreatePetWithInvalidJsonReturns400() throws Exception {
        mvc.perform(post("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json }"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void whenCreatePetWithMissingRequiredFieldReturns400() throws Exception {
        mvc.perform(post("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"petType":"DOG","trackerType":"SMALL","inZone":true}
                """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"))
            .andExpect(jsonPath("$.details.ownerId").exists());
    }

    @Test
    void whenCreatePetWithInvalidEnumReturns400() throws Exception {
        mvc.perform(post("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"petType":"INVALID","trackerType":"SMALL","ownerId":123,"inZone":true}
                """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetOutOfZoneSummaryThenReturns200AndSummaryList() throws Exception {
        var summary1 = new OutsideZoneSummaryDTO(PetType.CAT, TrackerType.SMALL, 5);
        var summary2 = new OutsideZoneSummaryDTO(PetType.DOG, TrackerType.BIG, 10);

        when(petService.outOfZoneSummary()).thenReturn(List.of(summary1, summary2));

        mvc.perform(get("/api/pets/out-of-zone-summary").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].petType").value("CAT"))
            .andExpect(jsonPath("$[0].trackerType").value("SMALL"))
            .andExpect(jsonPath("$[0].count").value(5))
            .andExpect(jsonPath("$[1].petType").value("DOG"))
            .andExpect(jsonPath("$[1].trackerType").value("BIG"))
            .andExpect(jsonPath("$[1].count").value(10));
    }

    @Test
    void whenGetOutOfZoneSummaryEmptyThenReturns200AndEmptyList() throws Exception {
        when(petService.outOfZoneSummary()).thenReturn(List.of());

        mvc.perform(get("/api/pets/out-of-zone-summary").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }
}
