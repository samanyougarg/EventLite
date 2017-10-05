package uk.ac.man.cs.eventlite.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.MediaType;

@Controller
@RequestMapping("/")
public class HomepageControllerRest {
	
	@RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
	public String getRoot(Model model, UriComponentsBuilder b, UriComponentsBuilder r, 
			UriComponentsBuilder k) {

		UriComponents link = b.path("/").build();
		UriComponents events = r.path("/events").build();
		UriComponents venues = k.path("/venues").build();
		model.addAttribute("self_link", link.toUri());
		model.addAttribute("events_link", events.toUri());
		model.addAttribute("venues_link", venues.toUri());
		return "home/index";
	}

}