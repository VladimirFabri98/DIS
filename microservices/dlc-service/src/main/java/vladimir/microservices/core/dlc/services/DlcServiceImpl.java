package vladimir.microservices.core.dlc.services;

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
	public Dlc getDlc(int dlcId) {
		LOG.debug("/Dlc return the found dlc for dlcId={}", dlcId);

        if (dlcId < 1) throw new InvalidInputException("Invalid dlc: " + dlcId);
		
		return new Dlc(dlcId,dlcId,"Wrath of the Lich King",20,serviceUtil.getServiceAddress());
	}
}
