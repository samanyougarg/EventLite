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
import uk.ac.man.cs.eventlite.entities.Venue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

@Controller
@RequestMapping("/venues")
public class VenuesControllerRest {

	@Autowired
	private VenueService venueService;
	
	@Autowired
	private EventService eventService;
	
	@RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
	public String getVenues(Model model, UriComponentsBuilder b) {
		UriComponents link = b.path("/venues").build();
		model.addAttribute("self_link", link.toUri());
		model.addAttribute("venues", venueService.findAll());
		return "venues/index";
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public String getVenueDetails(@PathVariable(value = "id") long id, Model model, UriComponentsBuilder b) {
		Venue venue = venueService.findOne(id);
		
		if (venue == null) {
			return "redirect:/venues";
		}
		
		UriComponents link = b.path("/venues/{id}").buildAndExpand(venue.getId());
		model.addAttribute("self_link", link.toUri());
		model.addAttribute("venue", venue);

		return "venues/details";
	}
	
	@RequestMapping(value = "/{id}/events", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public String getVenueEvents(@PathVariable(value = "id") long id, Model model, UriComponentsBuilder b) {
		Venue venue = venueService.findOne(id);
		
		if (venue == null) {
			return "redirect:/venues";
		}
		
		String link = b.path("/").build().toString();
		model.addAttribute("self_link", link);
		model.addAttribute("venue", venue);
		model.addAttribute("events", eventService.findAllByVenue(venue));

		return "venues/events";
	}

}