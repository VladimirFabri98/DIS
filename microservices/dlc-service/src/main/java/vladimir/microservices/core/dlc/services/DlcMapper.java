package vladimir.microservices.core.dlc.services;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import vladimir.api.core.dlc.Dlc;
import vladimir.microservices.core.dlc.persistence.DlcEntity;

@Mapper
public interface DlcMapper {

	@Mappings({
		@Mapping(target = "serviceAddress", ignore = true)
	})
	Dlc entityToApi(DlcEntity entity);
	
	@Mappings({
		@Mapping(target = "id", ignore = true),
		@Mapping(target = "version", ignore = true)
	})
	DlcEntity apiToEntity(Dlc api);
	
	List<Dlc> entityListToApiList(List<DlcEntity> entity);
    List<DlcEntity> apiListToEntityList(List<Dlc> api);
	
}
