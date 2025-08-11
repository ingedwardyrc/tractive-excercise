package com.tractive.pettracker.api;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PetController.class)
class PetControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void whenCreatePetReturns201AndLocation() throws Exception {
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
