package searchrequests.controller;

import org.hibernate.annotations.ManyToAny;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;
import searchrequests.persistence.PropertyApplicationRepository;

import java.io.FileInputStream;
import java.io.IOException;
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
                .andExpect(jsonPath("$.creationSource").value("PORTAL"));

        Mockito.verify(repo, times(1)).save(any());
    }
}
