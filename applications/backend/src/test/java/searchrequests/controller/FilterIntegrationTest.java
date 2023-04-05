package searchrequests.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import searchrequests.model.CreationSource;
import searchrequests.model.PropertyApplication;
import searchrequests.model.Status;
import searchrequests.persistence.PropertyApplicationRepository;

import java.util.List;
import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilterIntegrationTest {

    @Autowired
    private WebTestClient client;

    @Autowired
    private PropertyApplicationRepository repo;

    @BeforeEach
    void setUp() {
        repo.deleteAll();
        repo.save(createDummyApplication(1, "blah@blub.de", "blah", 0, false, 12, Status.CREATED, CreationSource.MANUAL));
        repo.save(createDummyApplication(2, "blah@blub.de", "hello", 1, false, 13, Status.DECLINED, CreationSource.PORTAL));
        repo.save(createDummyApplication(3, "user@blub.de", "user", 2, true, 12, Status.CREATED, CreationSource.PORTAL));
        repo.save(createDummyApplication(4, "user@blub.de", "user", 2, false, 10, Status.CREATED, CreationSource.MANUAL));
        repo.save(createDummyApplication(5, "user@blub.de", "user", 3, false, 11, Status.CREATED, CreationSource.PORTAL));
        repo.save(createDummyApplication(6, "user2@blub.de", "user2", 2, true, 13, Status.INVITED, CreationSource.MANUAL));
        repo.save(createDummyApplication(7, "user3@blub.de", "user3", 2, false, 20, Status.DECLINED, CreationSource.MANUAL));
    }

    @ParameterizedTest
    @MethodSource("getFilterParameters")
    void testFilter(String queryParameters, List<Long> expectedIds) {
        client.get().uri("/api/v1/applications/" + queryParameters).exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBody()
                .jsonPath("$").isArray();

        // TODO: assert IDs
    }

    private static Stream<Arguments> getFilterParameters() {
        return Stream.of(
                Arguments.of("", List.of(1, 2, 3, 4, 5, 6, 7)),
                Arguments.of("?email=blah@blub.de", List.of(1, 2)),
                Arguments.of("?propertyId=12", List.of(1, 3)),
                Arguments.of("?wbsPresent=true", List.of(3, 6)),
                Arguments.of("?numberOfPersons=3", List.of(5)),
                Arguments.of("?status=CREATED", List.of(1, 3, 4, 5)),
                Arguments.of("?email=user@blub.de&numberOfPersons=2", List.of(3, 4)),
                Arguments.of("?email=user@blub.de&numberOfPersons=2&wbsPresent=false&propertyId=10&status=CREATED", List.of(4))
        );
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
