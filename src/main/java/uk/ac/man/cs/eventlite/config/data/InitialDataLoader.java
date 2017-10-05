package uk.ac.man.cs.eventlite.config.data;

import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@Component
@Profile({ "default", "test" })
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> 
{
	private final static Logger log = LoggerFactory.getLogger(InitialDataLoader.class);

	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;

	private static SimpleDateFormat dateFormat;
	private static SimpleDateFormat timeFormat;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) 
	{
		if (eventService.count() > 0) 
		{
			log.info("Database already populated. Skipping data initialization.");
			return;
		} // if
		
		dateFormat = new SimpleDateFormat("dd/mm/yyyy");
		timeFormat = new SimpleDateFormat("hh:mm");

		// Build and save initial models here.
		Venue vA = new Venue();
		vA.setName("Tiger Tiger");
		vA.setCapacity(300);
		vA.setAddress("27 Withy Grove");
		vA.setCity("Manchester");
		vA.setZip("M4 2BS");
		vA.setLat("53.4852");
		vA.setLng("-2.2407");
		venueService.save(vA);
		
		Venue vB = new Venue();
		vB.setName("Sound Control");
		vB.setCapacity(154);
		vB.setAddress("1 New Wakefield St");
		vB.setCity("Manchester");
		vB.setZip("M1 5NP");
		vB.setLat("53.47352");
		vB.setLng("-2.242641");
		venueService.save(vB);
		
		Venue vC = new Venue();
		vC.setName("Fifth NightClub");
		vC.setCapacity(217);
		vC.setAddress("121 Princess St");
		vC.setCity("Manchester");
		vC.setZip("M1 7AG");
		vC.setLat("53.475427");
		vC.setLng("-2.237559");
		venueService.save(vC);
		
		Venue vD = new Venue();
		vD.setName("The Alchemist");
		vD.setCapacity(111);
		vD.setAddress("1 New York St");
		vD.setCity("Manchester");
		vD.setZip("M1 4HD");
		vD.setLat("53.48015");
		vD.setLng("-2.239725");
		venueService.save(vD);
		
		
		String description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.";
		
		Event eAlpha = createEvent("Event Alpha", description, "12:30", "11/06/2017", vA);
		Event eBeta = createEvent("Event Beta", description, "10:00", "11/06/2017", vC);
		Event eApple = createEvent("Event Apple", description, "00:00", "11/06/2017", vD);
		Event eFormer = createEvent("Event Former", description, "11:00", "11/01/2017", vB);
		Event ePrevious = createEvent("Event Previous", description, "18:30", "11/01/2017", vA);
		Event ePast = createEvent("Event Past", description, "17:00", "10/01/2017", vA);
		
		eventService.save(eAlpha);
		eventService.save(eBeta);
		eventService.save(eApple);
		eventService.save(eFormer);
		eventService.save(ePrevious);
		eventService.save(ePast);
	} // onApplicationEvent
	
	private Event createEvent(String name, String description, String time, String date, Venue venue) 
	{
		Event event = new Event();
		
		event.setName(name);
		event.setDescription(description);
		event.setVenue(venue);
		
		try 
		{
			event.setDate(dateFormat.parse(date));
			event.setTime(timeFormat.parse(time));
		} 
		catch (Exception e) 
		{}
		
		return event;
	} // createEvent
	
} // InitialDataLoader
