package searchrequests.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import searchrequests.model.CreationSource;
import searchrequests.model.PropertyApplication;
import searchrequests.model.Status;

import static org.assertj.core.api.Assertions.assertThat;

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
        var application = new PropertyApplication();
        application.setEmail("dummy@blub.de");
        application.setFirstName("hello");
        application.setLastName("world");
        application.setCreationSource(CreationSource.MANUAL);
        application.setStatus(Status.CREATED);

        repo.save(application);

        assertThat(repo.findAll())
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("firstName", "hello")
                .hasFieldOrPropertyWithValue("lastName", "world")
                .hasFieldOrPropertyWithValue("status", Status.CREATED)
                .hasFieldOrPropertyWithValue("creationSource", CreationSource.MANUAL);
    }
}
