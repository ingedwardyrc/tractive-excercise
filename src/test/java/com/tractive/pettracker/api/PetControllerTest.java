package com.tractive.pettracker.api;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tractive.pettracker.api.dto.PetRequestDTO;
import com.tractive.pettracker.api.dto.PetResponseDTO;
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

}
