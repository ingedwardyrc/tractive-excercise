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

		var createdPetBody = result.getResponse().getContentAsString();
		var createdPetJson = objectMapper.readTree(createdPetBody);
		var id = createdPetJson.get("id").asLong();

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
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.error").value("NOT_FOUND"))
			.andExpect(jsonPath("$.message").value("Pet 9999999 not found"));
	}

	@Test
	void whenCreateCatWithInvalidTrackerThenReturns400WithValidationPayload() throws Exception {
		var invalidRequestJson = """
            {"petType":"CAT","trackerType":"MEDIUM","ownerId":123,"inZone":false,"lostTracker":false}
        """;

		mockMvc.perform(
				post("/api/pets")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(invalidRequestJson))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error").value("VALIDATION_FAILED"))
			.andExpect(jsonPath("$.details.trackerType").value("Cats can only have SMALL or BIG trackers"));
	}

	@Test
	void whenListPetsThenReturns200AndListOfPets() throws Exception {
		var pet1Request = """
        {"petType":"CAT","trackerType":"SMALL","ownerId":1,"inZone":true,"lostTracker":false}
    """;
		var pet1Result = mockMvc.perform(
				post("/api/pets")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(pet1Request))
			.andExpect(status().isCreated())
			.andReturn();
		var pet1Id = objectMapper.readTree(pet1Result.getResponse().getContentAsString()).get("id").asLong();

		var pet2Request = """
        {"petType":"DOG","trackerType":"BIG","ownerId":2,"inZone":false,"lostTracker":true}
    """;
		var pet2Result = mockMvc.perform(
				post("/api/pets")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(pet2Request))
			.andExpect(status().isCreated())
			.andReturn();
		var pet2Id = objectMapper.readTree(pet2Result.getResponse().getContentAsString()).get("id").asLong();

		mockMvc.perform(get("/api/pets").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(2))
			.andExpect(jsonPath("$[0].id").value(pet1Id))
			.andExpect(jsonPath("$[0].petType").value("CAT"))
			.andExpect(jsonPath("$[0].trackerType").value("SMALL"))
			.andExpect(jsonPath("$[1].id").value(pet2Id))
			.andExpect(jsonPath("$[1].petType").value("DOG"))
			.andExpect(jsonPath("$[1].trackerType").value("BIG"));
	}

	@Test
	void whenListPetsAndEmptyThenReturns200AndEmptyArray() throws Exception {
		mockMvc.perform(get("/api/pets").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	void whenUpdatePetTrackingDataThenReturns200AndUpdatedPet() throws Exception {
		var createRequest = """
        {"petType":"DOG","trackerType":"SMALL","ownerId":10,"inZone":false}
    """;

		var createResult = mockMvc.perform(
				post("/api/pets")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(createRequest))
			.andExpect(status().isCreated())
			.andReturn();

		var createdPetJson = objectMapper.readTree(createResult.getResponse().getContentAsString());
		var id = createdPetJson.get("id").asLong();

		var updateRequest = """
        {"petType":"DOG","trackerType":"MEDIUM","ownerId":10,"inZone":true,"lostTracker":true}
    """;

		mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/pets/{id}", id)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(updateRequest))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(id))
			.andExpect(jsonPath("$.petType").value("DOG"))
			.andExpect(jsonPath("$.trackerType").value("MEDIUM"))
			.andExpect(jsonPath("$.ownerId").value(10))
			.andExpect(jsonPath("$.inZone").value(true));
	}

	@Test
	void whenGetOutOfZoneSummaryThenReturns200AndSummaryList() throws Exception {
		var pet1Request = """
        {"petType":"CAT","trackerType":"SMALL","ownerId":100,"inZone":false,"lostTracker":true}
    """;
		var pet2Request = """
        {"petType":"DOG","trackerType":"BIG","ownerId":101,"inZone":false,"lostTracker":false}
    """;
		var pet3Request = """
        {"petType":"DOG","trackerType":"BIG","ownerId":102,"inZone":true,"lostTracker":false}
    """;

		mockMvc.perform(post("/api/pets")
				.contentType(MediaType.APPLICATION_JSON)
				.content(pet1Request))
			.andExpect(status().isCreated());

		mockMvc.perform(post("/api/pets")
				.contentType(MediaType.APPLICATION_JSON)
				.content(pet2Request))
			.andExpect(status().isCreated());

		mockMvc.perform(post("/api/pets")
				.contentType(MediaType.APPLICATION_JSON)
				.content(pet3Request))
			.andExpect(status().isCreated());

		mockMvc.perform(get("/api/pets/out-of-zone-summary")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(2)) // We expect 2 groups: CAT-SMALL and DOG-BIG out of zone
			.andExpect(jsonPath("$[?(@.petType=='CAT' && @.trackerType=='SMALL')].count").value(1))
			.andExpect(jsonPath("$[?(@.petType=='DOG' && @.trackerType=='BIG')].count").value(1));
	}
}
