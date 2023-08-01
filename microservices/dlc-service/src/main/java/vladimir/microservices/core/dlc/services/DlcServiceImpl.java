package vladimir.microservices.core.dlc.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import vladimir.api.core.dlc.DlcService;
import vladimir.api.core.dlc.Dlc;
import vladimir.util.exceptions.InvalidInputException;
import vladimir.util.http.ServiceUtil;

@RestController
public class DlcServiceImpl implements DlcService {

	private static final Logger LOG = LoggerFactory.getLogger(DlcServiceImpl.class);
	private final ServiceUtil serviceUtil;
	
	@Autowired
	public DlcServiceImpl(ServiceUtil serviceUtil) {
		this.serviceUtil = serviceUtil;
	}
	
	@Override
	public List<Dlc>getDlcs(int gameId) {
		
		LOG.debug("/DLC return the found dlc for gameId={}", gameId);
        if (gameId < 1) throw new InvalidInputException("Invalid gameId: " + gameId);
        
        if (gameId == 200) {
            LOG.debug("No dlcs found for gameId: {}", gameId);
            return  new ArrayList<>();
        }
        
        List<Dlc> list = new ArrayList<>();
        list.add(new Dlc(1,gameId,"Chaos warriors",9,serviceUtil.getServiceAddress()));
        list.add(new Dlc(2,gameId,"The Warden & the Paunch",10,serviceUtil.getServiceAddress()));
        list.add(new Dlc(3,gameId,"Forge of the Chaos Dwarfs",25,serviceUtil.getServiceAddress()));
        
        LOG.debug("/dlc response size: {}", list.size());
        
        return list;
	}

}
