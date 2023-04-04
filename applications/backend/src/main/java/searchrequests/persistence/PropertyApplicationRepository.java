package searchrequests.persistence;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import searchrequests.model.PropertyApplication;

public interface PropertyApplicationRepository extends CrudRepository<PropertyApplication, Long> {

}
