package searchrequests.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import searchrequests.model.CreationSource;
import searchrequests.model.PropertyApplication;
import searchrequests.model.Status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.jpa.domain.Specification.where;
import static searchrequests.persistence.FilterSpecifications.*;

@DataJpaTest
class PropertyApplicationRepositoryTest {

    @Autowired
    PropertyApplicationRepository repo;

    @BeforeEach
    void setUp() {
        repo.deleteAll();
    }

    @Test
    @DisplayName("test if h2 setup is configured correctly")
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
    @DisplayName("cover all filter specifications to catch field name changes")
    void testFilter() {
        // Create two applications for different properties
        repo.save(createDummyApplication());
        var application2 = createDummyApplication();
        application2.setId(2);
        application2.setPropertyId(2);
        application2.setNumberOfPersons(2);
        application2.setWbsPresent(true);
        repo.save(application2);

        var result = repo.findAll(where(hasPropertyId(2)).and(hasEmail("dummy@blub.de")).and(hasStatus(Status.CREATED)));

        assertThat(result)
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("propertyId", 2L);

        result = repo.findAll(where(isWbsPresent(false)).and(hasNumberOfPersons(2)));

        assertThat(result).isEmpty();
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
