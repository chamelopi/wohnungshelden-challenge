package searchrequests.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import searchrequests.dto.PortalApplicationDto;
import searchrequests.dto.UiApplicationDto;
import searchrequests.model.PropertyApplication;

@Mapper
public interface ApplicationMapper {
    @Mapping(target = "creationSource", expression = "java(searchrequests.model.CreationSource.PORTAL)")
    PropertyApplication portalApplicationToPropertyApplication(PortalApplicationDto portalApplication);

    @Mapping(target = "creationSource", expression = "java(searchrequests.model.CreationSource.MANUAL)")
    PropertyApplication uiApplicationToPropertyApplication(UiApplicationDto uiApplication);
}
