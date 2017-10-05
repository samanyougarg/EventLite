package uk.ac.man.cs.eventlite.controllers;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@Controller
@RequestMapping("/events")
public class EventsControllerWeb 
{
	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;

	// TWITTER API IMPLEMENTATION \\
	
	private Twitter twitter;
	private ConnectionRepository connectionRepository;

	@Inject
	public EventsControllerWeb(Twitter twitter, ConnectionRepository connectionRepository) 
	{
		this.connectionRepository = connectionRepository;
		this.twitter = twitter;
	} // EventsControllerWeb

	@RequestMapping(method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
	public String getAllEvents(Model model) 
	{
		// TWITTER FUNCTION START \\
		if (connectionRepository.findPrimaryConnection(Twitter.class) == null) 
		{
			model.addAttribute("notSignedIn", "notSignedIn");
		} // if
		else 
		{
			List<Tweet> tweets = twitter.timelineOperations().getUserTimeline(5);

			Map<String, String> tweetContents = new LinkedHashMap<String, String>();

			for (Tweet tweet : tweets) 
			{
				tweetContents.put(tweet.getCreatedAt().toString(), tweet.getText());
			} // for
			
			model.addAttribute("user", twitter.userOperations().getScreenName());
			model.addAttribute("tweetContents", tweetContents);
			
		} // else
		
		// TWITTER END \\ 

		List<Event> events = eventService.findAllByOrderByDateDesc();
		
		List<Event> eventspast = eventService.findPastEvents(false);
		
		List<Event> eventsupcoming = eventService.findUpcomingEvents(false);

		// GOOGLE MAPS FUNCTION \\

		List<Map<String, String>> markers = new ArrayList<Map<String, String>>();

		for (Event event : eventsupcoming) 
		{
			Map<String, String> center = new HashMap<String, String>();

			center.put("lat", event.getVenue().getLat());
			center.put("lng", event.getVenue().getLng());
			center.put("label", event.getName());

			markers.add(center);
		} 
		model.addAttribute("markers", markers);
		// GMAPS END

		model.addAttribute("events", events);
		model.addAttribute("eventspast", eventspast);
		model.addAttribute("eventsupcoming", eventsupcoming);

		return "events/index";
		
	} // getAllEvents
	
	// SEARCHING EVENTS FUNCTION \\

	@RequestMapping(value = "/s", method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
	public String getSearchedEvents(@RequestParam(value = "name", required = true) String name, Model model) {
		List<Event> events;
		if (name != "") {
			// Matcher for future events
			Iterable<Event> eventListFutureIterable = eventService.findUpcomingEvents(true);
			Iterator<Event> eventListFuture = eventListFutureIterable.iterator();
			Pattern pattern = Pattern.compile("(?i)\\b" + name + "\\b");

			while (eventListFuture.hasNext()) 
			{
				Event event = eventListFuture.next();
				Matcher matcher = pattern.matcher(event.getName());

				if (!(matcher.find()))
					eventListFuture.remove();
			}
			
			// Matcher for future events
			Iterable<Event> eventListPastIterable = eventService.findPastEvents(true);
			Iterator<Event> eventListPast = eventListPastIterable.iterator();

			while (eventListPast.hasNext()) 
			{
				Event event = eventListPast.next();
				Matcher matcher = pattern.matcher(event.getName());

				if (!(matcher.find()))
					eventListPast.remove();
			}
			events = (List<Event>) eventListFutureIterable;
			model.addAttribute("eventsupcoming", (List<Event>) eventListFutureIterable);
			model.addAttribute("eventspast", (List<Event>) eventListPastIterable);
		} else {
			events = eventService.findAllByOrderByDateDesc();
			//Added for upcoming and past events
			model.addAttribute("eventsupcoming", eventService.findUpcomingEvents(false));
			model.addAttribute("eventspast", eventService.findPastEvents(false));
		}
		
		// TWITTER START
		if (connectionRepository.findPrimaryConnection(Twitter.class) == null) {
			model.addAttribute("notSignedIn", "notSignedIn");
		} else {
			List<Tweet> tweets = twitter.timelineOperations().getUserTimeline(5);

			Map<String, String> tweetContents = new HashMap<String, String>();

			for (Tweet tweet : tweets) {
				tweetContents.put(tweet.getCreatedAt().toString(), tweet.getText());
			}
			model.addAttribute("user", twitter.userOperations().getScreenName());
			model.addAttribute("tweetContents", tweetContents);
		} // TWITTER END

		// GMAPS START

		List<Map<String, String>> markers = new ArrayList<Map<String, String>>();

		for (Event event : events) {
			Map<String, String> center = new HashMap<String, String>();

			center.put("lat", event.getVenue().getLat());
			center.put("lng", event.getVenue().getLng());
			center.put("label", event.getName());

			markers.add(center);
		}
		model.addAttribute("markers", markers);
		// GMAPS END

		return "events/index";
	} // getSearchedEvents

	// ADDING EVENTS FUNCTIONS \\
	
	@RequestMapping(method = RequestMethod.GET, value = "/add", produces = { MediaType.TEXT_HTML_VALUE })
	public String addPage(Model model) 
	{
		model.addAttribute("venues", venueService.findAll());

		return "events/add";
	} // addPage

	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = { MediaType.TEXT_HTML_VALUE })
	public String addEvent(@RequestParam("name") String name,
	                       @RequestParam("description") String description,
	   	                   @RequestParam("date") String date,
		                   @RequestParam("time") String time,
		                   @RequestParam("venue") String venueId,
		                   Model model) throws Exception
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
		
		Event event = new Event();
		event.setName(name);
		event.setDescription(description);
		
		try {
			event.setVenue(venueService.findOne(Long.parseLong(venueId)));
		} catch (Exception e) {
			event.setVenue(null);
		}
		
		try {
			event.setDate(dateFormat.parse(date));
		} catch (Exception e) {
			event.setDate(null);
		}
		
		try {
			event.setTime(timeFormat.parse(time));
		} catch (Exception e) {
			event.setTime(null);
		}
		
		String error = Event.validate(event);
		
		if (error.length() > 0) 
		{
			model.addAttribute("event", event);
			model.addAttribute("venues", venueService.findAll());
			model.addAttribute("error", error);

			return "events/add";
		} // if
		
		eventService.save(event);

		return "redirect:/events/" + event.getId();
	}

	// EVENT DETAILS FUNCTION \\
	
	@RequestMapping(value = "/{id}", method = RequestMethod.POST, produces = { MediaType.TEXT_HTML_VALUE })
	public String getEventDetails(@PathVariable(value = "id") long id, Model model,
			@ModelAttribute("tweet") String tweet) 
	{
		if (connectionRepository.findPrimaryConnection(Twitter.class) == null) 
		{
			return "redirect:/connect/twitter";
		} // if
		twitter.timelineOperations().updateStatus(tweet);
		model.addAttribute("tweet", tweet);

		Event event = eventService.findOne(id);
		Venue venue = event.getVenue();
		
		// GOOGLE MAPS START \\

 	    List<Map<String, String>> markers = new ArrayList<Map<String, String>>();
 	    
 	    Map<String, String> center = new HashMap<String, String>();
 	    
 	    center.put("lat", venue.getLat());
 	    center.put("lng", venue.getLng());
 	    center.put("label", event.getName());
 	    
 	    markers.add(center);
 	    
 	    model.addAttribute("center", center);
 	    model.addAttribute("markers", markers);
		// GMAPS END
		model.addAttribute("event", event);
		model.addAttribute("venue", venue);

		return "events/details";
	} // getEventDetails

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
	public String getEventDetails(@PathVariable(value = "id") long id, Model model) 
	{
		Event event = eventService.findOne(id);

		if (event == null) 
		{
			return "redirect:/events";
		} // if

		Venue venue = event.getVenue();

		// GOOGLE MAPS START \\

 	    List<Map<String, String>> markers = new ArrayList<Map<String, String>>();
 	    
 	    Map<String, String> center = new HashMap<String, String>();
 	    
 	    center.put("lat", venue.getLat());
 	    center.put("lng", venue.getLng());
 	    center.put("label", event.getName());

 	    markers.add(center);
 	    
 	    model.addAttribute("center", center);
 	    model.addAttribute("markers", markers);
		// GOOGLE MAPS END \\
		
		model.addAttribute("event", event);
		model.addAttribute("venue", venue);

		return "events/details";
	} //  getEventDetails

	// DELETING THE EVENT FUNCTIONS \\ 
	
	@RequestMapping(value = "/{id}/delete", method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
	public String deleteEventWeb(@PathVariable("id") long id) {
		eventService.delete(id);

		return "redirect:/events";
	} // deleteEventWeb

	@RequestMapping(value = "/{id}/delete", method = RequestMethod.DELETE)
	public @ResponseBody String deleteEventApi(@PathVariable("id") long id) {
		eventService.delete(id);

		return "/events";
	} // deleteEventApi

	// UPDATE THE EVENTS FUNCTION \\ 
	
	@RequestMapping(value = "/{id}/update", method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
	public String updateEvent(@PathVariable("id") long id, Model model) 
	{
		Event event = eventService.findOne(id);

		if (event != null) 
		{
			model.addAttribute("event", event);
			model.addAttribute("venues", venueService.findAll());

			return "events/update";
		} // if

		return "redirect:/events";
	} // updateEvent

	@RequestMapping(value = "/{id}/update", method = RequestMethod.POST, produces = { MediaType.TEXT_HTML_VALUE })
	public String updateEvent(@PathVariable("id") long id,
	                          @RequestParam("name") String name,
	                          @RequestParam("description") String description,
	   	                      @RequestParam("date") String date,
		                      @RequestParam("time") String time,
		                      @RequestParam("venue") String venueId,
		                      Model model) throws Exception
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
		
		Event event = eventService.findOne(id);
		event.setName(name);
		event.setDescription(description);
		
		try {
			event.setVenue(venueService.findOne(Long.parseLong(venueId)));
		} catch (Exception e) {
			event.setVenue(null);
		}
		
		try {
			event.setDate(dateFormat.parse(date));
		} catch (Exception e) {
			event.setDate(null);
		}
		
		try {
			event.setTime(timeFormat.parse(time));
		} catch (Exception e) {
			event.setTime(null);
		}
		
		String error = Event.validate(event);
		
		if (error.length() > 0) 
		{
			model.addAttribute("event", event);
			model.addAttribute("venues", venueService.findAll());
			model.addAttribute("error", error);

			return "events/update";
		} // if
		
		eventService.update(event);

		return "redirect:/events/" + id;
	}
	
} // EventsControllerWeb Class