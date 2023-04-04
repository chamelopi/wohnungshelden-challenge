package searchrequests.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import searchrequests.model.CreationSource;
import searchrequests.model.PropertyApplication;
import searchrequests.model.Status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.jpa.domain.Specification.where;
import static searchrequests.persistence.FilterSpecifications.hasEmail;
import static searchrequests.persistence.FilterSpecifications.hasPropertyId;

@DataJpaTest
public class PropertyApplicationRepositoryTest {

    @Autowired
    PropertyApplicationRepository repo;

    @BeforeEach
    void setUp() {
        repo.deleteAll();
    }

    // Mainly tests that h2 db is configured correctly for now
    @Test
    void testSaveAndFind() {
        repo.save(createDummyApplication());

        assertThat(repo.findAll())
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("firstName", "hello")
                .hasFieldOrPropertyWithValue("lastName", "world")
                .hasFieldOrPropertyWithValue("status", Status.CREATED)
                .hasFieldOrPropertyWithValue("creationSource", CreationSource.MANUAL);
    }

    @Test
    void testFilterForProperty() {
        // Create two applications for different properties
        repo.save(createDummyApplication());
        var application2 = createDummyApplication();
        application2.setId(2);
        application2.setPropertyId(2);
        repo.save(application2);

        var result = repo.findAll(where(hasPropertyId(2)).and(hasEmail("dummy@blub.de")));

        assertThat(result)
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("propertyId", 2L);
    }

    private PropertyApplication createDummyApplication() {
        var application = new PropertyApplication();
        application.setEmail("dummy@blub.de");
        application.setFirstName("hello");
        application.setLastName("world");
        application.setCreationSource(CreationSource.MANUAL);
        application.setStatus(Status.CREATED);
        application.setPropertyId(1);
        return application;
    }
}
