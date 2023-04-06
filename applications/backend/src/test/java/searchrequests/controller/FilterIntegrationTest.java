package searchrequests.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import searchrequests.model.CreationSource;
import searchrequests.model.PropertyApplication;
import searchrequests.model.Status;
import searchrequests.persistence.PropertyApplicationRepository;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

// To prevent conflicts with other tests in the future
@DirtiesContext
// To allow @BeforeAll method to be non-static
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilterIntegrationTest {

    @Autowired
    private WebTestClient client;

    @Autowired
    private PropertyApplicationRepository repo;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    void setUp() {
        repo.deleteAll();
        repo.save(createDummyApplication(1, "blah@blub.de", "blah", 0, false, 12, Status.CREATED, CreationSource.MANUAL, Instant.now().minus(10, ChronoUnit.SECONDS)));
        repo.save(createDummyApplication(2, "blah@blub.de", "hello", 1, false, 13, Status.DECLINED, CreationSource.PORTAL, Instant.now()));
        repo.save(createDummyApplication(3, "user@blub.de", "user", 2, true, 12, Status.CREATED, CreationSource.PORTAL));
        repo.save(createDummyApplication(4, "user@blub.de", "user", 2, false, 10, Status.CREATED, CreationSource.MANUAL));
        repo.save(createDummyApplication(5, "user@blub.de", "user", 3, false, 11, Status.CREATED, CreationSource.PORTAL));
        repo.save(createDummyApplication(6, "user2@blub.de", "user2", 2, true, 13, Status.INVITED, CreationSource.MANUAL));
        repo.save(createDummyApplication(7, "user3@blub.de", "user3", 2, false, 20, Status.DECLINED, CreationSource.MANUAL));

        mapper.registerModule(new JavaTimeModule());
    }

    @ParameterizedTest
    @MethodSource("getFilterParameters")
    @DisplayName("test combinations of filter parameters")
    void testFilter(String queryParameters, List<Long> expectedIds) throws IOException {
        var result = client.get().uri("/api/v1/applications/" + queryParameters).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBody()
                .jsonPath("$").isArray()
                .returnResult();

        var applications = mapper.readValue(result.getResponseBody(), new TypeReference<List<PropertyApplication>>() {});

        assertThat(applications).extracting(PropertyApplication::getId)
                // Note: we don't care about the sort order in this test
                .containsExactlyInAnyOrderElementsOf(expectedIds);
    }

    @Test
    @DisplayName("test that an invalid filter parameter results in 400 Bad Request")
    void testInvalidFilter() {
        client.get().uri("/api/v1/applications/?invalid=filter&email=a@b.de").exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("test that default sort order is newest applications first")
    void testDefaultSort() {
        client.get().uri("/api/v1/applications/?email=blah@blub.de").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                // Expect ID 2 to be returned first (newer entry)
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(2)
                .jsonPath("$[1].id").isEqualTo(1);
    }

    private static Stream<Arguments> getFilterParameters() {
        return Stream.of(
                Arguments.of("", List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L)),
                Arguments.of("?email=blah@blub.de", List.of(1L, 2L)),
                Arguments.of("?propertyId=12", List.of(1L, 3L)),
                Arguments.of("?wbsPresent=true", List.of(3L, 6L)),
                Arguments.of("?numberOfPersons=3", List.of(5L)),
                Arguments.of("?status=CREATED", List.of(1L, 3L, 4L, 5L)),
                Arguments.of("?email=user@blub.de&numberOfPersons=2", List.of(3L, 4L)),
                Arguments.of("?email=user@blub.de&numberOfPersons=2&wbsPresent=false&propertyId=10&status=CREATED", List.of(4L)),
                Arguments.of("?email=blah@blub.de&page=0&size=10", List.of(2L, 1L))
        );
    }

    private PropertyApplication createDummyApplication(long id, String email, String lastName, int numberOfPersons, boolean wbsPresent, long propertyId, Status status, CreationSource creationSource, Instant creationTimestamp) {
        var application = createDummyApplication(id, email, lastName, numberOfPersons, wbsPresent, propertyId, status, creationSource);
        application.setCreationTimestamp(creationTimestamp);
        return application;
    }

    private PropertyApplication createDummyApplication(long id, String email, String lastName, int numberOfPersons, boolean wbsPresent, long propertyId, Status status, CreationSource creationSource) {
        var appl = new PropertyApplication();
        appl.setId(id);
        appl.setEmail(email);
        appl.setLastName(lastName);
        appl.setNumberOfPersons(numberOfPersons);
        appl.setWbsPresent(wbsPresent);
        appl.setPropertyId(propertyId);
        appl.setStatus(status);
        appl.setCreationSource(creationSource);
        return appl;
    }
}
