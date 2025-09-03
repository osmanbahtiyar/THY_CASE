package com.osbah.thycase.infra.route.in.rest;

import com.osbah.thycase.ThyCaseApplication;
import com.osbah.thycase.common.PostgresTestContainerBase;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;

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

    @Test
    @DisplayName("GET /api/routes/find - 1 -> 3 without date should return empty")
    void findRoutes_WhenCalledWithoutDbData_ShouldReturnEmpty() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/routes/find")
                                .param("originLocationId", "1")
                                .param("destinationLocationId", "3")
                                .param("date", "2025-03-12")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}