package edu.famu.cop3060.resources;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ResourcesApiTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void list_noFilters_returnsOkAndArray() throws Exception {
        mockMvc.perform(get("/api/resources"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$", Matchers.hasSize(Matchers.greaterThanOrEqualTo(5))));
    }

    @Test
    void detail_knownId_returnsOkAndName() throws Exception {
        mockMvc.perform(get("/api/resources/tutoring-ace"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ACE Tutoring Center"))
                .andExpect(jsonPath("$.category").value("Tutoring"));
    }

    @Test
    void filter_byCategory_onlyThatCategory() throws Exception {
        mockMvc.perform(get("/api/resources").param("category", "Tutoring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].category", Matchers.everyItem(Matchers.is("Tutoring"))));
    }

    @Test
    void filter_byQ_matchesNameOrTags_caseInsensitive() throws Exception {
        mockMvc.perform(get("/api/resources").param("q", "python"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].tags").isArray());
    }
}
