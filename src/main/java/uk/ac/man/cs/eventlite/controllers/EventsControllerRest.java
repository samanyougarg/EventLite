package uk.ac.man.cs.eventlite.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

@Controller
@RequestMapping("/events")
public class EventsControllerRest {
	
	@Autowired
	private EventService eventService;
	
	@RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
	public String hanuman(Model model, UriComponentsBuilder b) {

		UriComponents link = b.path("/").build();
		model.addAttribute("self_link", link.toUri());
		model.addAttribute("events", eventService.findAllByOrderByDateDesc());
		return "events/index";
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public String getEventDetails(@PathVariable(value = "id") long id, Model model, UriComponentsBuilder b) {
		Event event = eventService.findOne(id);
		
		if (event == null) {
			return "redirect:/events";
		}
		
		String link = b.path("/").build().toString();
		model.addAttribute("self_link", link + "events/" + event.getId());
		model.addAttribute("venue_self_link", link + "venues/" + event.getVenue().getId());

		Venue venue = event.getVenue();

		model.addAttribute("event", event);
		model.addAttribute("venue", venue);

		return "events/details";
	}

}
