package searchrequests.controller;

import org.mapstruct.factory.Mappers;
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
        return persistPropertyApplication(propertyApplication);
    }

    @PostMapping
    @RequestMapping(value = "/portal/applications/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PropertyApplication> createApplicationFromPortal(@RequestBody @Valid PortalApplicationDto portalApplication) {
        var propertyApplication = Mappers.getMapper(ApplicationMapper.class).portalApplicationToPropertyApplication(portalApplication);
        return persistPropertyApplication(propertyApplication);
    }

    ResponseEntity<PropertyApplication> persistPropertyApplication(PropertyApplication propertyApplication) {
        propertyApplication.setStatus(Status.CREATED);
        propertyApplication = repo.save(propertyApplication);

        // Build a URL to the newly created resource
        var url = ServletUriComponentsBuilder.fromCurrentServletMapping()
                .path("api/v1/applications/{id}")
                .buildAndExpand(propertyApplication.getId())
                .toUri();

        return ResponseEntity.created(url).body(propertyApplication);
    }
}
