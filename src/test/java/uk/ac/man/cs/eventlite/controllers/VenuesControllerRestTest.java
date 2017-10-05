package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import uk.ac.man.cs.eventlite.TestParent;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@AutoConfigureMockMvc
public class VenuesControllerRestTest extends TestParent {

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private VenueService venueService;
	
	@Autowired
	private EventService eventService;
	
	private Venue venue;
	private Event event;
	
	@Before
	public void setUp() {
		if (venue == null) {
			venue = venueService.findAll().iterator().next();
			event = eventService.findAllByVenue(venue).iterator().next();
		}
	}
	
	@Test
	public void testGetAllVenues() throws Exception {
		assertNotNull(venue);
		
		String basePath = "$.venues[?(@.id == " + venue.getId() + ")]";
		
		mvc.perform(get("/venues").accept(MediaType.APPLICATION_JSON))
		   .andExpect(status().isOk())
		   .andExpect(view().name("venues/index"))
		   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
		   .andExpect(jsonPath("$.title", equalTo("EventLite Venues")))
		   .andExpect(jsonPath("$._self", equalTo("http://localhost/venues")))
		   .andExpect(jsonPath(basePath + ".id", hasItem((int) venue.getId())))
		   .andExpect(jsonPath(basePath + ".name", hasItem(venue.getName())))
		   .andExpect(jsonPath(basePath + ".capacity", hasItem(venue.getCapacity())))
		   .andExpect(jsonPath(basePath + "._self", hasItem("http://localhost/venues/" + venue.getId())));
	}
	
	@Test
	public void testVenueDetails() throws Exception {
		assertNotNull(venue);
		
		mvc.perform(get("/venues/" + venue.getId()).accept(MediaType.APPLICATION_JSON))
		   .andExpect(status().isOk())
		   .andExpect(view().name("venues/details"))
		   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
		   .andExpect(jsonPath("$.title", equalTo("EventLite Venue 1")))
		   .andExpect(jsonPath("$._self", equalTo("http://localhost/venues/" + venue.getId())))
		   .andExpect(jsonPath("$.id", equalTo((int) venue.getId())))
		   .andExpect(jsonPath("$.name", equalTo(venue.getName())))
		   .andExpect(jsonPath("$.capacity", equalTo(venue.getCapacity())));
	}
	
	@Test
	public void testVenueEvents() throws Exception {
		assertNotNull(venue);
		assertNotNull(event);
		
		String basePath = "$.events[?(@.id == " + event.getId() + ")]";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		
		mvc.perform(get("/venues/" + venue.getId() + "/events").accept(MediaType.APPLICATION_JSON))
		   .andExpect(status().isOk())
		   .andExpect(view().name("venues/events"))
		   .andExpect(content().contentType(MediaType.APPLICATION_JSON))
		   .andExpect(jsonPath("$.title", equalTo("EventLite Venue Events")))
		   .andExpect(jsonPath("$.id", equalTo((int) venue.getId())))
		   .andExpect(jsonPath("$._self", equalTo("http://localhost/venues/" + venue.getId() + "/events")))
		   .andExpect(jsonPath(basePath + ".id", hasItem((int) event.getId())))
		   .andExpect(jsonPath(basePath + ".name", hasItem(event.getName())))
		   .andExpect(jsonPath(basePath + ".date", hasItem(dateFormat.format(event.getDate()))))
		   .andExpect(jsonPath(basePath + ".time", hasItem(timeFormat.format(event.getTime()))))
		   .andExpect(jsonPath(basePath + "._self", hasItem("http://localhost/events/" + event.getId())));
	}
}
