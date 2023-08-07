package vladimir.microservices.core.dlc.services;

import java.util.List;

import vladimir.api.core.dlc.Dlc;
import vladimir.microservices.core.dlc.persistence.DlcEntity;

//@Mapper(componentModel = "spring")
public interface DlcMapper {

//	@Mappings({
//		@Mapping(target = "serviceAddress", ignore = true)
//	})
	Dlc entityToApi(DlcEntity entity);
	
//	@Mappings({
//		@Mapping(target = "id", ignore = true),
//		@Mapping(target = "version", ignore = true)
//	})
	DlcEntity apiToEntity(Dlc api);
	
	List<Dlc> entityListToApiList(List<DlcEntity> entity);
    List<DlcEntity> apiListToEntityList(List<Dlc> api);
}
