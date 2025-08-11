package com.tractive.pettracker.api;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tractive.pettracker.api.dto.PetRequestDTO;
import com.tractive.pettracker.api.dto.PetResponseDTO;
import com.tractive.pettracker.application.exceptions.NotFoundException;
import com.tractive.pettracker.application.service.PetService;
import com.tractive.pettracker.domain.PetType;
import com.tractive.pettracker.domain.TrackerType;
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
}
