package searchrequests.controller;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import searchrequests.dto.PortalApplicationDto;
import searchrequests.dto.UiApplicationDto;
import searchrequests.dto.mapper.ApplicationMapper;
import searchrequests.model.PropertyApplication;
import searchrequests.model.Status;
import searchrequests.persistence.PropertyApplicationRepository;

import javax.validation.Valid;
import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class PropertyApplicationController {

    // TODO/Nice to have: ControllerAdvice to return nice error messages for frontend

    @Autowired
    PropertyApplicationRepository repo;

    @PostMapping
    @RequestMapping(value = "/ui/applications/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PropertyApplication> createApplicationFromUi(@RequestBody @Valid UiApplicationDto uiApplication) {
        var propertyApplication = Mappers.getMapper(ApplicationMapper.class).uiApplicationToPropertyApplication(uiApplication);
        return createPropertyApplication(propertyApplication);
    }

    @PostMapping
    @RequestMapping(value = "/portal/applications/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PropertyApplication> createApplicationFromPortal(@RequestBody @Valid PortalApplicationDto portalApplication) {
        var propertyApplication = Mappers.getMapper(ApplicationMapper.class).portalApplicationToPropertyApplication(portalApplication);
        return createPropertyApplication(propertyApplication);
    }

    // This could be refactored into a business layer later if necessary
    private ResponseEntity<PropertyApplication> createPropertyApplication(PropertyApplication propertyApplication) {
        propertyApplication.setStatus(Status.CREATED);
        propertyApplication.setCreationTimestamp(Instant.now());
        propertyApplication = repo.save(propertyApplication);

        // We should use the MDC if we wanted log these IDs in more places, but this is ok for now
        log.info("PropertyApplication created with propertyId={}, applicationId={} and source={}",
                propertyApplication.getPropertyId(), propertyApplication.getId(), propertyApplication.getCreationSource().name());

        // Build a URL to the newly created resource
        var url = ServletUriComponentsBuilder.fromCurrentServletMapping()
                .path("api/v1/applications/{id}")
                .buildAndExpand(propertyApplication.getId())
                .toUri();

        return ResponseEntity.created(url).body(propertyApplication);
    }
}
