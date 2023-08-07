package vladimir.microservices.core.event.services;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
	public List<Event> getEvents(int gameId) {
		try {
			date = sdf.parse("25-11-2005").getTime();
		} catch (ParseException e) {
			LOG.debug("Invalid date");
		}
		
		LOG.debug("/Event return the found event for gameId={}", gameId);
        if (gameId < 1) throw new InvalidInputException("Invalid gameId: " + gameId);
        
        if (gameId == 200) {
            LOG.debug("No events found for gameId: {}", gameId);
            return  new ArrayList<>();
        }
        
        List<Event> list = new ArrayList<>();
        list.add(new Event(1, gameId, "Conference", "Blizzcon", new Date(date), serviceUtil.getServiceAddress()));
        list.add(new Event(1, gameId, "Conference", "Twitchcon", new Date(date), serviceUtil.getServiceAddress()));
        
        LOG.debug("/review response size: {}", list.size());
        
        return list;
	}


	@Override
	public Event createEvent(Event body) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void deleteEvents(int gameId) {
		// TODO Auto-generated method stub
		
	}
}
