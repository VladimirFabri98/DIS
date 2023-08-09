package vladimir.microservices.core.dlc.services;

import static reactor.core.publisher.Mono.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import vladimir.api.core.dlc.Dlc;
import vladimir.api.core.dlc.DlcService;
import vladimir.microservices.core.dlc.persistence.DlcEntity;
import vladimir.microservices.core.dlc.persistence.DlcRepository;
import vladimir.util.exceptions.InvalidInputException;
import vladimir.util.exceptions.NotFoundException;
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
	public Flux<Dlc> getDlcs(int gameId) {
		if (gameId < 1) throw new InvalidInputException("Invalid gameId: " + gameId);

        return repository.findByGameId(gameId)
            .switchIfEmpty(error(new NotFoundException("No dlcs found for gameId: " + gameId)))
            .log()
            .map(e -> mapper.entityToApi(e))
            .map(e -> {e.setServiceAddress(serviceUtil.getServiceAddress()); return e;});
	}

	@Override
	public Dlc createDlc(Dlc body) {
		if (body.getGameId() < 1) throw new InvalidInputException("Invalid gameId: " + body.getGameId());

        DlcEntity entity = mapper.apiToEntity(body);
        Mono<Dlc> newEntity = repository.save(entity)
            .log()
            .onErrorMap(
                DuplicateKeyException.class,
                ex ->  new InvalidInputException("Duplicate key, Game Id: " + body.getGameId() + ", Dlc Id:" + body.getDlcId()))
            .map(e -> mapper.entityToApi(e));

        return newEntity.block();
	}

	@Override
	public void deleteDlcs(int gameId) {
		if(gameId < 1) throw new InvalidInputException("Invalid gameId: " + gameId);
		LOG.debug("deleteDlc: tries to delete an entity with gameId: {}", gameId);
		repository.deleteAll(repository.findByGameId(gameId)).block();

	}

}
