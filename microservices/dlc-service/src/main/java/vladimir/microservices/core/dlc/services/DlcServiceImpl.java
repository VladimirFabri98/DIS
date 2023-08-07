package vladimir.microservices.core.dlc.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

import vladimir.api.core.dlc.Dlc;
import vladimir.api.core.dlc.DlcService;
import vladimir.microservices.core.dlc.persistence.DlcEntity;
import vladimir.microservices.core.dlc.persistence.DlcRepository;
import vladimir.util.exceptions.InvalidInputException;
import vladimir.util.http.ServiceUtil;

@RestController
public class DlcServiceImpl implements DlcService {

	private static final Logger LOG = LoggerFactory.getLogger(DlcServiceImpl.class);
	private final ServiceUtil serviceUtil;
	private final DlcMapperImpl mapper;
	private final DlcRepository repository;
	
	@Autowired
	public DlcServiceImpl(ServiceUtil serviceUtil, DlcMapperImpl mapper, DlcRepository repository) {
		this.serviceUtil = serviceUtil;
		this.mapper = mapper;
		this.repository = repository;
	}
	
	@Override
	public List<Dlc> getDlcs(int gameId) {
        if (gameId < 1) throw new InvalidInputException("Invalid gameId: " + gameId);
        
        List<DlcEntity> listEntity = repository.findByGameId(gameId);
        List<Dlc> listApi = mapper.entityListToApiList(listEntity);
        listApi.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));
        
        
        LOG.debug("/dlc response size: {}", listApi.size());
        
        return listApi;
	}

	@Override
	public Dlc createDlc(Dlc body) {
		try {
			DlcEntity entity = mapper.apiToEntity(body);
			DlcEntity newEntity = repository.save(entity);
			Dlc response =  mapper.entityToApi(newEntity);
			
			LOG.debug("createDlc: created a dlc entity: {}/{}", body.getGameId(),body.getDlcId());
			
			return response;
		} catch (DuplicateKeyException e) {
			throw new InvalidInputException("Duplicate key, gameId: "+ body.getGameId() + ", dlcId: " + body.getDlcId());
		}
		
	}

	@Override
	public void deleteDlcs(int gameId) {
		LOG.debug("deleteDlcs: tries to delete dlcs for the game with gameId: {}", gameId);
		repository.deleteAll(repository.findByGameId(gameId));
		
	}

}
