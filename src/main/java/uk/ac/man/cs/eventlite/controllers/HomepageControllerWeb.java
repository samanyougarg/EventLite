package uk.ac.man.cs.eventlite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;

@Controller
@RequestMapping("/")
public class HomepageControllerWeb 
{
	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;

	// RETURNING THE EVENTS FUNCTION \\ 
	
	@RequestMapping(method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
	public String getAllEvents(Model model) 
	{
		model.addAttribute("events", eventService.findUpcomingEvents(false));
		model.addAttribute("venues", venueService.findTopVenues());

		return "home/index";
	} // getAllEvents
	
} // HomepageControllerWeb Class