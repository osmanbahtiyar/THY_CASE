package com.osbah.thycase.infra.location.in.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osbah.thycase.ThyCaseApplication;
import com.osbah.thycase.common.PostgresTestContainerBase;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ThyCaseApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@Disabled
class LocationControllerIntegrationTest extends PostgresTestContainerBase {

    @Autowired
    MockMvc mockMvc;

    private final ObjectMapper om = new ObjectMapper();

    @Test
    @DisplayName("POST /api/location - Valid Location")
    void create_WhenLocationValid_ShouldReturnCreated() throws Exception {
        final String valid = """
                {
                  "city": "Istanbul",
                  "country": "Turkey",
                  "locationCode": "UNKAIR",
                  "name": "Unknown Airport"
                }
                """;
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/locations")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(valid))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /api/location - LocationCode length less than 3")
    void create_WhenLocationCodeLengthLessThanThree_ShouldReturnError() throws Exception {
        final String invalidJson = """
                {
                  "city": "Istanbul",
                  "country": "Turkey",
                  "locationCode": "SA",
                  "name": "Sabiha Gokcen Airport"
                }
                """;
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/locations")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/location - Name Blank")
    void create_WhenNameBlank_ShouldReturnError() throws Exception {
        final String invalidJson = """
                {
                  "city": "Istanbul",
                  "country": "Turkey",
                  "locationCode": "SAW",
                  "name": ""
                }
                """;
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/locations")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/location - Country Blank")
    void create_WhenCountryBlank_ShouldReturnError() throws Exception {
        final String invalidJson = """
                {
                  "city": "Istanbul",
                  "country": "",
                  "locationCode": "SAW",
                  "name": "Sabiha Gokcen Airport"
                }
                """;
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/locations")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/location - City Blank")
    void create_WhenCityBlank_ShouldReturnError() throws Exception {
        final String invalidJson = """
                {
                  "country": "Turkey",
                  "locationCode": "SAW",
                  "name": "Sabiha Gokcen Airport"
                }
                """;
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/locations")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/location - Location Already Exists")
    void create_WhenLocationAlreadyExists_ShouldReturnError() throws Exception {
        final String invalidJson = """
                {
                  "city": "Istanbul",
                  "country": "Turkey",
                  "locationCode": "SAW",
                  "name": "Sabiha Gokcen Airport"
                }
                """;
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/locations")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(invalidJson))
                .andReturn();
        JsonNode list = om.readTree(result.getResponse().getContentAsString());
        assertThat(list.get("title").asText()).isEqualTo("Location Conflict Error");
        assertThat(list.get("status").asText()).isEqualTo("409");
        assertThat(list.get("detail").asText()).isEqualTo("Location already exists:SAW");
    }

    @Test
    @DisplayName("PUT /api/location/1 - Location not found")
    void update_WhenLocationNotFound_ShouldReturnError() throws Exception {
        final String updateRequest = """
                {
                  "city": "Istanbul",
                  "country": "Turkey",
                  "locationCode": "SAW",
                  "name": "Sabiha Gokcen Airport New"
                }
                """;
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/locations/99999")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(updateRequest))
                .andReturn();
        JsonNode list = om.readTree(result.getResponse().getContentAsString());
        assertThat(list.get("title").asText()).isEqualTo("Not Found Error");
        assertThat(list.get("status").asText()).isEqualTo("404");
        assertThat(list.get("detail").asText()).isEqualTo("Location not found with id:99999");
    }
}