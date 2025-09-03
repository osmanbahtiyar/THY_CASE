package com.osbah.thycase.infra.transportation.in.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osbah.thycase.ThyCaseApplication;
import com.osbah.thycase.common.PostgresTestContainerBase;
import com.osbah.thycase.infra.location.out.jpa.LocationJpaRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
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
class TransportationControllerIntegrationTest extends PostgresTestContainerBase {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    LocationJpaRepository locationJpaRepository;

    private long TAKSIM_ID, IST_ID;
    private final ObjectMapper om = new ObjectMapper();

    @BeforeEach
    void resolveIdsFromSeed() {
        TAKSIM_ID  = locationJpaRepository.findByCode("TAKSIM").getId();
        IST_ID     = locationJpaRepository.findByCode("IST").getId();
    }

    @Test
    @DisplayName("POST /api/transportation - Valid Transportation")
    void create_WhenTransportationValid_ShouldReturnCreated() throws Exception {
        final String request = """
                {
                  "destinationLocationId": 2,
                  "originLocationId": 1,
                  "transportationType": "FLIGHT",
                  "operatingDays": [1,2,3,4,5]
                }
                """;
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/transportations")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(request))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /api/transportation - Invalid Operation Day")
    void create_WhenInvalidOperationDayGiven_ShouldReturnError() throws Exception {
        final String request = """
                {
                  "destinationLocationId": 2,
                  "originLocationId": 1,
                  "transportationType": "FLIGHT",
                  "operatingDays": [1,2,3,4,5,9]
                }
                """;
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/transportations")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(request))
                .andReturn();

        JsonNode list = om.readTree(result.getResponse().getContentAsString());
        assertThat(list.get("title").asText()).isEqualTo("Validation failed");
        assertThat(list.get("status").asText()).isEqualTo("400");
        assertThat(list.get("detail").asText()).isEqualTo("Request body validation failed");
        assertThat(list.get("violations").get(0).get("message").asText()).isEqualTo("Operating days must be between 1 and 7");
    }

    @Test
    @DisplayName("POST /api/transportation - Invalid Transportation Type Given")
    void create_WhenInvalidTransportationTypeGiven_ShouldReturnError() throws Exception {
        final String request = """
                {
                  "destinationLocationId": 2,
                  "originLocationId": 1,
                  "transportationType": "INVALID",
                  "operatingDays": [1,2,3,4,5]
                }
                """;
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/transportations")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/transportation - Origin and Destination Same")
    void create_WhenOriginAndDestinationSameGiven_ShouldReturnError() throws Exception {
        final String request = """
                {
                  "destinationLocationId": 1,
                  "originLocationId": 1,
                  "transportationType": "BUS",
                  "operatingDays": [1,2,3,4,5]
                }
                """;
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/transportations")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(request))
                .andReturn();

        JsonNode list = om.readTree(result.getResponse().getContentAsString());
        assertThat(list.get("title").asText()).isEqualTo("Validation failed");
        assertThat(list.get("status").asText()).isEqualTo("400");
        assertThat(list.get("detail").asText()).isEqualTo("Request body validation failed");
        assertThat(list.get("violations").get(0).get("message").asText()).isEqualTo("Origin and destination location must be different");
    }

    @Test
    @DisplayName("POST /api/transportation - Already Exists")
    void create_WhenTransportationAlreadyExists_ShouldReturnError() throws Exception {
        String request = """
                {
                  "destinationLocationId": {},
                  "originLocationId": {},
                  "transportationType": "SUBWAY",
                  "operatingDays": [1,2,3,4,5,6,7]
                }
                """;
        request = StringUtils.replaceOnce(request, "{}", String.valueOf(IST_ID));
        request = StringUtils.replaceOnce(request, "{}", String.valueOf(TAKSIM_ID));
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/transportations")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(request))
                .andReturn();

        JsonNode list = om.readTree(result.getResponse().getContentAsString());
        assertThat(list.get("title").asText()).isEqualTo("Location Conflict Error");
        assertThat(list.get("errorCode").asText()).isEqualTo("1003");
        assertThat(list.get("status").asText()).isEqualTo("409");
        assertThat(list.get("detail").asText()).isEqualTo(String.format("Transportation already exists by originId:%d, destinationId:%d, type:SUBWAY", TAKSIM_ID, IST_ID));
    }
}