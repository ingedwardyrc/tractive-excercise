package com.tractive.pettracker.api;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PetTrackerApplicationTests {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	void whenCreatePetThenReturns201AndLocationAndBody() throws Exception {
		var requestJson = """
            {"petType":"CAT","trackerType":"SMALL","ownerId":123,"inZone":true,"lostTracker":false}
        """;

		var result = mockMvc.perform(
				post("/api/pets")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(requestJson))
			.andExpect(status().isCreated())
			.andExpect(header().string("Location", containsString("/api/pets/")))
			.andExpect(jsonPath("$.id", notNullValue()))
			.andExpect(jsonPath("$.petType").value("CAT"))
			.andExpect(jsonPath("$.trackerType").value("SMALL"))
			.andExpect(jsonPath("$.ownerId").value(123))
			.andExpect(jsonPath("$.inZone").value(true))
			.andExpect(jsonPath("$.lostTracker").value(false))
			.andReturn();

		var createdBody = result.getResponse().getContentAsString();
		var createdJson = objectMapper.readTree(createdBody);
		var id = createdJson.get("id").asLong();

		mockMvc.perform(get("/api/pets/{id}", id).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(id))
			.andExpect(jsonPath("$.petType").value("CAT"))
			.andExpect(jsonPath("$.trackerType").value("SMALL"))
			.andExpect(jsonPath("$.ownerId").value(123))
			.andExpect(jsonPath("$.inZone").value(true))
			.andExpect(jsonPath("$.lostTracker").value(false));
	}

	@Test
	void whenGetByIdThenReturns200AndPet() throws Exception {
		var createPetRequest = """
            {"petType":"DOG","trackerType":"MEDIUM","ownerId":77,"inZone":false}
        """;

		var createPetResult = mockMvc.perform(
			post("/api/pets")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(createPetRequest))
			.andExpect(status().isCreated())
			.andReturn();

		var createdPet = objectMapper.readTree(createPetResult.getResponse().getContentAsString());
		var id = createdPet.get("id").asLong();

		mockMvc.perform(get("/api/pets/{id}", id).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(id))
			.andExpect(jsonPath("$.petType").value("DOG"))
			.andExpect(jsonPath("$.trackerType").value("MEDIUM"))
			.andExpect(jsonPath("$.ownerId").value(77))
			.andExpect(jsonPath("$.inZone").value(false))
			.andExpect(jsonPath("$.lostTracker").doesNotExist());
	}

	@Test
	void whenGetUnknownIdThenReturns404() throws Exception {
		mockMvc.perform(get("/api/pets/{id}", 9_999_999L).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}
}
