package searchrequests.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;
import searchrequests.persistence.PropertyApplicationRepository;

import java.nio.file.Files;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {PropertyApplicationController.class})
class PropertyApplicationControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    PropertyApplicationRepository repo;

    @Test
    @DisplayName("test creation of applications from UI")
    void testCreateFromUi() throws Exception {
        var testdata = Files.readAllBytes(ResourceUtils.getFile("classpath:testdata/uiapplication.json").toPath());

        doAnswer(inv -> inv.getArgument(0)).when(repo).save(any());

        mvc.perform(post("/api/v1/ui/applications/")
                        .content(testdata)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.creationTimestamp").isNotEmpty())
                .andExpect(jsonPath("$.creationSource").value("MANUAL"));

        Mockito.verify(repo, times(1)).save(any());
    }

    @Test
    @DisplayName("test creation of applications from an external portal application")
    void testCreateFromPortal() throws Exception {
        var testdata = Files.readAllBytes(ResourceUtils.getFile("classpath:testdata/portalapplication.json").toPath());

        doAnswer(inv -> inv.getArgument(0)).when(repo).save(any());

        mvc.perform(post("/api/v1/portal/applications/")
                        .content(testdata)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.creationSource").value("PORTAL"))
                .andExpect(jsonPath("$.creationTimestamp").isNotEmpty())
                .andExpect(jsonPath("$.earliestMoveInDate").value("2023-06-01"));

        Mockito.verify(repo, times(1)).save(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"uiapplication_invalid_email.json", "uiapplication_missing_first_name.json", "portalapplication_missing_property_id.json"})
    @DisplayName("test if validation failure causes error responses and no persistence")
    void testValidation(String testdataFile) throws Exception {
        var testdata = Files.readAllBytes(ResourceUtils.getFile("classpath:testdata/" + testdataFile).toPath());

        mvc.perform(post("/api/v1/ui/applications/")
                        .content(testdata)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.verify(repo, never()).save(any());
    }
}
