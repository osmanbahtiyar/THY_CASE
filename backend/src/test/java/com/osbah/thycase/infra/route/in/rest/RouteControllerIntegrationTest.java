package com.osbah.thycase.infra.route.in.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osbah.thycase.ThyCaseApplication;
import com.osbah.thycase.common.PostgresTestContainerBase;
import com.osbah.thycase.infra.location.out.jpa.LocationJpaRepository;
import org.junit.jupiter.api.BeforeEach;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ThyCaseApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
class RouteControllerIntegrationTest extends PostgresTestContainerBase {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    LocationJpaRepository locationJpaRepository;

    private long TAKSIM_ID, IST_ID, ESB_ID, WEMBLEY_ID, LHR_ID;
    private final ObjectMapper om = new ObjectMapper();

    @BeforeEach
    void resolveIdsFromSeed() {
        TAKSIM_ID  = locationJpaRepository.findByCode("TAKSIM").getId();
        IST_ID     = locationJpaRepository.findByCode("IST").getId();
        ESB_ID     = locationJpaRepository.findByCode("ESB").getId();
        WEMBLEY_ID = locationJpaRepository.findByCode("WEMBLEY").getId();
        LHR_ID = locationJpaRepository.findByCode("LHR").getId();
    }

    @Test
    @DisplayName("GET /api/routes/find - TAKSIM_ID -> IST_ID without date should return empty")
    void findRoutes_WhenNoRouteExists_ShouldReturnEmpty() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/routes/find")
                                .param("originLocationId", String.valueOf(TAKSIM_ID))
                                .param("destinationLocationId", String.valueOf(IST_ID))
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/routes/find - TAKSIM_ID -> WEMBLEY_ID without date should 3 routes")
    void findRoutes_WhenThreeRouteExists_ShouldReturnThreeRoute() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/routes/find")
                                .param("originLocationId", String.valueOf(TAKSIM_ID))
                                .param("destinationLocationId", String.valueOf(WEMBLEY_ID))
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @DisplayName("GET /api/routes/find - TAKSIM_ID -> TAKSIM_ID without date should return error")
    void findRoutes_WhenOriginAndDestinationSame_ShouldReturnError() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/routes/find")
                                .param("originLocationId", String.valueOf(TAKSIM_ID))
                                .param("destinationLocationId", String.valueOf(TAKSIM_ID))
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/routes/find - IST_ID -> ESB_ID without date should return single flight route")
    void findRoutes_WhenRouteContainsOnlyFlight_ShouldReturnSingleFlight() throws Exception {
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/routes/find")
                                .param("originLocationId", String.valueOf(IST_ID))
                                .param("destinationLocationId", String.valueOf(ESB_ID))
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andReturn();
        JsonNode list = om.readTree(result.getResponse().getContentAsString());
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).get("transportations").size()).isEqualTo(1);
        assertThat(list.get(0).get("transportations").get(0).get("transportationType").asText()).isEqualTo("FLIGHT");
    }

    @Test
    @DisplayName("GET /api/routes/find - IST_ID -> LHR_ID with non operating date should return empty")
    void findRoutes_WhenRouteExistsButNonOperationOnDate_ShouldReturnEmpty() throws Exception {
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/routes/find")
                                .param("originLocationId", String.valueOf(IST_ID))
                                .param("destinationLocationId", String.valueOf(LHR_ID))
                                .param("date", "2025-09-06")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andReturn();
        JsonNode list = om.readTree(result.getResponse().getContentAsString());
        assertThat(list.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("GET /api/routes/find - IST_ID -> LHR_ID with operating date should return single route")
    void findRoutes_WhenRouteExistsOnOperationOnDate_ShouldReturnSingleRoute() throws Exception {
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/routes/find")
                                .param("originLocationId", String.valueOf(IST_ID))
                                .param("destinationLocationId", String.valueOf(LHR_ID))
                                .param("date", "2025-09-07")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andReturn();
        JsonNode list = om.readTree(result.getResponse().getContentAsString());
        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("GET /api/routes/find - LHR_ID -> WEMBLEY_ID without operatingDate and nonFlight route exists should return empty")
    void findRoutes_WhenNonFlightRouteExists_ShouldReturnEmpty() throws Exception {
        //LHR -> Subway -> LONCC -> Bus -> WEMBLEY
        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/routes/find")
                                .param("originLocationId", String.valueOf(LHR_ID))
                                .param("destinationLocationId", String.valueOf(WEMBLEY_ID))
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andReturn();
        JsonNode list = om.readTree(result.getResponse().getContentAsString());
        assertThat(list.size()).isEqualTo(0);
    }

}