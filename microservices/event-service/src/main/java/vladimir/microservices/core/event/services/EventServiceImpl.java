package vladimir.microservices.core.event.services;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import vladimir.api.core.event.Event;
import vladimir.api.core.event.EventService;
import vladimir.util.exceptions.InvalidInputException;
import vladimir.util.http.ServiceUtil;

@RestController
public class EventServiceImpl implements EventService {

	private static final Logger LOG = LoggerFactory.getLogger(EventServiceImpl.class);
	private final ServiceUtil serviceUtil;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	private long date;
	
	
	
	@Autowired
	public EventServiceImpl(ServiceUtil serviceUtil) {
		this.serviceUtil = serviceUtil;
	}
	
	@Override
	public Event getEvent(int eventId) {
		try {
			date = sdf.parse("25-11-2005").getTime();
		} catch (ParseException e) {
			LOG.debug("Invalid date");
		}
		LOG.debug("/Event return the found event for eventId={}", eventId);

        if (eventId < 1) throw new InvalidInputException("Invalid event: " + eventId);
		
		return new Event(eventId, eventId, "Conference", "Blizzcon", new Date(date), serviceUtil.getServiceAddress());
	}
}
