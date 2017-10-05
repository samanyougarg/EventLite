package uk.ac.man.cs.eventlite.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.mockito.Mockito.*;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;
import uk.ac.man.cs.eventlite.TestParent;

@AutoConfigureMockMvc
public class VenuesControllerWebTest extends TestParent {

	@Autowired
	private MockMvc mvc;
	
	@Mock
	private VenueService venueService;
	
	@Mock
	private EventService eventService;
	
	@Mock
	private Venue venue;
	
	@InjectMocks
	private VenuesControllerWeb venuesController;

	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(venuesController).build();
	}

	@Test
	public void testGetAllvenues() throws Exception {
		when(venueService.findAll()).thenReturn(Collections.<Venue> emptyList());
		when(venueService.findAll()).thenReturn(Collections.<Venue> emptyList());
		
		mvc.perform(get("/venues").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		                          .andExpect(view().name("venues/index"));
	}
	
	@Test
	public void testVenueDetails() throws Exception {
		when(eventService.findAllByOrderByDateDesc()).thenReturn(Collections.<Event> emptyList());
		when(venueService.findOne(0)).thenReturn(venue);
		when(venueService.findOne(1)).thenReturn(null);
		
		mvc.perform(get("/venues/0").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		                            .andExpect(view().name("venues/details"));
		
		mvc.perform(get("/venues/1").accept(MediaType.TEXT_HTML)).andExpect(status().is(302))
		                            .andExpect(view().name("redirect:/venues"));
	}
	
	@Test
	public void testUpdateVenue() throws Exception {
		when(venueService.findOne(2)).thenReturn(new Venue());
		
		// get
		mvc.perform(get("/venues/2/update").accept(MediaType.TEXT_HTML))
		   .andExpect(status().is(200)).andExpect(view().name("venues/update"));

		// accepted
		mvc.perform(post("/venues/2/update").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.TEXT_HTML)
				.param("name", "Test Venue 2")
				.param("capacity", "20")
				.param("address", "kilburn building")
				.param("city", "manchester")
				.param("zip", "m13 9pl"))
				.andDo(print())
		        .andExpect(status().is(302)).andExpect(view().name("redirect:/venues/2"));
		
		// no name
		mvc.perform(post("/venues/2/update").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.TEXT_HTML)
				.param("name", "")
				.param("capacity", "20")
				.param("address", "kilburn building")
				.param("city", "manchester")
				.param("zip", "m13 9pl"))
		        .andExpect(status().is(200)).andExpect(view().name("venues/update"));
		
		// no capacity
		mvc.perform(post("/venues/2/update").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.TEXT_HTML)
				.param("name", "Test Venue 2")
				.param("capacity", "")
				.param("address", "kilburn building")
				.param("city", "manchester")
				.param("zip", "m13 9pl"))
				.andExpect(status().is(200)).andExpect(view().name("venues/update"));
		
		// no address
		mvc.perform(post("/venues/2/update").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.TEXT_HTML)
				.param("name", "Test Venue 2")
				.param("capacity", "20")
				.param("address", "")
				.param("city", "manchester")
				.param("zip", "m13 9pl"))
				.andExpect(status().is(200)).andExpect(view().name("venues/update"));
		
		// no city
		mvc.perform(post("/venues/2/update").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.TEXT_HTML)
				.param("name", "Test Venue 2")
				.param("capacity", "20")
				.param("address", "kilburn building")
				.param("city", "")
				.param("zip", "m13 9pl"))
				.andExpect(status().is(200)).andExpect(view().name("venues/update"));
		
		// no postcode
		mvc.perform(post("/venues/2/update").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.TEXT_HTML)
				.param("name", "Test Venue 2")
				.param("capacity", "20")
				.param("address", "kilburn building")
				.param("city", "manchester")
				.param("zip", ""))
				.andExpect(status().is(200)).andExpect(view().name("venues/update"));
	}
	

	@Test
	public void testAddVenue() throws Exception {
		// get
		mvc.perform(get("/venues/add").accept(MediaType.TEXT_HTML))
		   .andExpect(status().is(200)).andExpect(view().name("venues/add"));

		// accepted
		mvc.perform(post("/venues/add").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.TEXT_HTML)
				.param("name", "Test Venue 2")
				.param("capacity", "20")
				.param("address", "kilburn building")
				.param("city", "manchester")
				.param("zip", "m13 9pl"))
		        .andExpect(status().is(302)).andExpect(view().name("redirect:/venues/0"));
		
		// no name
		mvc.perform(post("/venues/add").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.TEXT_HTML)
				.param("name", "")
				.param("capacity", "20")
				.param("address", "kilburn building")
				.param("city", "manchester")
				.param("zip", "m13 9pl"))
		        .andExpect(status().is(200)).andExpect(view().name("venues/add"));
		
		// no capacity
		mvc.perform(post("/venues/add").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.TEXT_HTML)
				.param("name", "Test Venue 2")
				.param("capacity", "")
				.param("address", "kilburn building")
				.param("city", "manchester")
				.param("zip", "m13 9pl"))
				.andExpect(status().is(200)).andExpect(view().name("venues/add"));
		
		// no address
		mvc.perform(post("/venues/add").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.TEXT_HTML)
				.param("name", "Test Venue 2")
				.param("capacity", "20")
				.param("address", "")
				.param("city", "manchester")
				.param("zip", "m13 9pl"))
				.andExpect(status().is(200)).andExpect(view().name("venues/add"));
		
		// no city
		mvc.perform(post("/venues/add").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.TEXT_HTML)
				.param("name", "Test Venue 2")
				.param("capacity", "20")
				.param("address", "kilburn building")
				.param("city", "")
				.param("zip", "m13 9pl"))
				.andExpect(status().is(200)).andExpect(view().name("venues/add"));
		
		// no postcode
		mvc.perform(post("/venues/add").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.TEXT_HTML)
				.param("name", "Test Venue 2")
				.param("capacity", "20")
				.param("address", "kilburn building")
				.param("city", "manchester")
				.param("zip", ""))
				.andExpect(status().is(200)).andExpect(view().name("venues/add"));
	}
	
	@Test
	public void testDeleteVenue() throws Exception {
		when(venueService.findOne(3)).thenReturn(venue);
		when(venueService.countEvents(3)).thenReturn(0);
		when(venueService.findOne(4)).thenReturn(venue);
		when(venueService.countEvents(4)).thenReturn(1);
		
		mvc.perform(get("/venues/3/delete").accept(MediaType.TEXT_HTML))
		   .andExpect(status().is(302))
		   .andExpect(view().name("redirect:/venues"));
		
		mvc.perform(get("/venues/4/delete").accept(MediaType.TEXT_HTML))
		   .andExpect(status().is(200))
		   .andExpect(view().name("venues/details"));
		
	}
	
}
