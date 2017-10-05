package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.text.SimpleDateFormat;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import uk.ac.man.cs.eventlite.TestParent;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@AutoConfigureMockMvc
public class EventsControllerRestTest extends TestParent {

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private EventService eventService;

	@Test
	public void testEvents() throws Exception {
		Event event = eventService.findAll().iterator().next();
		Venue venue = event.getVenue();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		String basePath = "$.events[?(@.id == " + event.getId() + ")]";
		
		assertNotNull(event);
		assertNotNull(venue);
		
		mvc.perform(get("/events").accept(MediaType.APPLICATION_JSON))
		   .andExpect(status().isOk())
		   .andExpect(view().name("events/index"))
		   .andExpect(jsonPath("$.title", equalTo("EventLite Events")))
		   .andExpect(jsonPath("$._self", equalTo("http://localhost/events")))
		   .andExpect(jsonPath(basePath + "._self", hasItem("http://localhost/events/" + event.getId())))
		   .andExpect(jsonPath(basePath + ".id", hasItem((int) event.getId())))
		   .andExpect(jsonPath(basePath + ".name", hasItem(event.getName())))
		   .andExpect(jsonPath(basePath + ".date", hasItem(dateFormat.format(event.getDate()))))
		   .andExpect(jsonPath(basePath + ".time", hasItem(timeFormat.format(event.getTime()))))
		   .andExpect(jsonPath(basePath + ".venue.id", hasItem((int) venue.getId())))
		   .andExpect(jsonPath(basePath + ".venue.name", hasItem(venue.getName())))
		   .andExpect(jsonPath(basePath + ".venue.capacity", hasItem(venue.getCapacity())))
		   .andExpect(jsonPath(basePath + ".venue._self", hasItem("http://localhost/venues/" + venue.getId())));
	}
	
	@Test
	public void testEventDetails() throws Exception {
		Event event = eventService.findAll().iterator().next();
		Venue venue = event.getVenue();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		
		assertNotNull(event);
		assertNotNull(venue);
		
		mvc.perform(get("/events/" + event.getId()).accept(MediaType.APPLICATION_JSON))
		   .andExpect(status().isOk())
		   .andExpect(view().name("events/details"))
		   .andExpect(jsonPath("$.title", equalTo("EventLite Event 1")))
		   .andExpect(jsonPath("$._self", equalTo("http://localhost/events/" + event.getId())))
		   .andExpect(jsonPath("$.id", equalTo((int) event.getId())))
		   .andExpect(jsonPath("$.name", equalTo(event.getName())))
		   .andExpect(jsonPath("$.date", equalTo(dateFormat.format(event.getDate()))))
		   .andExpect(jsonPath("$.time", equalTo(timeFormat.format(event.getTime()))))
		   .andExpect(jsonPath("$.venue.id", equalTo((int) venue.getId())))
		   .andExpect(jsonPath("$.venue.name", equalTo(venue.getName())))
		   .andExpect(jsonPath("$.venue.capacity", equalTo(venue.getCapacity())))
		   .andExpect(jsonPath("$.venue._self", equalTo("http://localhost/venues/" + venue.getId())));
	}
}
