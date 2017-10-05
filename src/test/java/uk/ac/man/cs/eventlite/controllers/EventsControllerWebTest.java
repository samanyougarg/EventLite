package uk.ac.man.cs.eventlite.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.mockito.Mockito.*;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import uk.ac.man.cs.eventlite.TestParent;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@AutoConfigureMockMvc
public class EventsControllerWebTest extends TestParent {

	@Autowired
	private MockMvc mvc;
	
	@Mock
	private EventService eventService;
	@Mock
	private VenueService venueService;
	
	@Mock
	private Event event;
	@Mock
	private Venue venue;
	
	@Mock
	private ConnectionRepository connectionRepo;
	
	@Mock
	Connection<Twitter> twitterConn;
	
	@Spy
	Twitter twitter = new TwitterTemplate( "fpWYVpMHeewHQykynw1NV4V76", "nk4bkG2tuWzpviqRNB41N7HIF9OmRDK018w559tOkrl0osSnj6", "857543983597322240-E9y4yZrOqXne6QnLsE46QxIaFRUXq1O", "0PzfbQePnpkYKYEzsMGUQe6MSYPvfVWEByGPTP37bklMn");
	
	@InjectMocks
	private EventsControllerWeb eventsController;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(eventsController).build();
		}

	@Test
	public void testGetAllEvents() throws Exception {
		when(eventService.findPastEvents(false)).thenReturn(Collections.<Event> emptyList());
		when(eventService.findUpcomingEvents(false)).thenReturn(Collections.<Event> emptyList());
		when(eventService.findAllByOrderByDateDesc()).thenReturn(Collections.<Event> emptyList());
		
		mvc.perform(get("/events").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		                          .andExpect(view().name("events/index"));
	}
	
	@Test
	public void testEventSearch() throws Exception {
		
	}
	
	@Test
	public void testEventDetails() throws Exception {
		when(eventService.findOne(0)).thenReturn(event);
		when(event.getVenue()).thenReturn(venue);
		
		mvc.perform(get("/events/0").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		                            .andExpect(view().name("events/details"));
	}
	
	@Test
	public void testEventDetailsRedirect() throws Exception {
		when(eventService.findOne(1)).thenReturn(null);
		
		mvc.perform(get("/events/1").accept(MediaType.TEXT_HTML)).andExpect(status().is(302))
		                            .andExpect(view().name("redirect:/events"));
	}
	
	@Test
	public void testUpdateEvent() throws Exception {
		when(eventService.findOne(0)).thenReturn(new Event());
		when(venueService.findOne(venue.getId())).thenReturn(venue);
		when(venueService.findAll()).thenReturn((Iterable<Venue>) Arrays.asList(venue));
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String date = format.format(new Date(new Date().getTime() + 3600*48*1000));
		String date1 = format.format(new Date(new Date().getTime() - 3600*48*1000));
		
		mvc.perform(get("/events/0/update").accept(MediaType.TEXT_HTML))
		    .andExpect(status().is(200)).andExpect(view().name("events/update"));
		
		// accepted
		mvc.perform(post("/events/0/update").contentType(MediaType.APPLICATION_FORM_URLENCODED)
		    .accept(MediaType.TEXT_HTML)
		    .param("name", "Test Event 2")
		    .param("description", "Test description")
		    .param("venue", "" + venue.getId())
		    .param("date", date)
		    .param("time", "00:00:00"))
		    .andExpect(status().is(302)).andExpect(view().name("redirect:/events/0"));
		
		// no name
		mvc.perform(post("/events/0/update").contentType(MediaType.APPLICATION_FORM_URLENCODED)
		    .accept(MediaType.TEXT_HTML)
		    .param("name", "")
		    .param("description", "Test description")
		    .param("venue", "" + venue.getId())
		    .param("date", date)
		    .param("time", "00:00:00"))
		    .andExpect(status().is(200)).andExpect(view().name("events/update"));
		
		// no description
		mvc.perform(post("/events/0/update").contentType(MediaType.APPLICATION_FORM_URLENCODED)
		    .accept(MediaType.TEXT_HTML)
		    .param("name", "Test Event 2")
		    .param("description", "")
		    .param("venue", "" + venue.getId())
		    .param("date", date)
		    .param("time", "00:00:00"))
		    .andExpect(status().is(200)).andExpect(view().name("events/update"));
		
		// no date
		mvc.perform(post("/events/0/update").contentType(MediaType.APPLICATION_FORM_URLENCODED)
		    .accept(MediaType.TEXT_HTML)
		    .param("name", "Test Event 2")
		    .param("description", "Test description")
		    .param("venue", "" + venue.getId())
		    .param("date", "")
		    .param("time", "00:00:00"))
		    .andExpect(status().is(200)).andExpect(view().name("events/update"));
		
		// no time
		mvc.perform(post("/events/0/update").contentType(MediaType.APPLICATION_FORM_URLENCODED)
		    .accept(MediaType.TEXT_HTML)
		    .param("name", "Test Event 2")
		    .param("description", "Test description")
		    .param("venue", "" + venue.getId())
		    .param("date", date)
		    .param("time", ""))
		    .andExpect(status().is(200)).andExpect(view().name("events/update"));
		
		// no venue
		mvc.perform(post("/events/0/update").contentType(MediaType.APPLICATION_FORM_URLENCODED)
		    .accept(MediaType.TEXT_HTML)
		    .param("name", "Test Event 2")
		    .param("description", "Test description")
	 	    .param("venue", "")
		    .param("date", date)
		    .param("time", ""))
		    .andExpect(status().is(200)).andExpect(view().name("events/update"));
		
		// past date
		mvc.perform(post("/events/0/update").contentType(MediaType.APPLICATION_FORM_URLENCODED)
		    .accept(MediaType.TEXT_HTML)
		    .param("name", "Test Event 2")
		    .param("description", "Test description")
		    .param("venue", "" + venue.getId())
		    .param("date", date1)
		    .param("time", ""))
		    .andExpect(status().is(200)).andExpect(view().name("events/update"));
	}	
	
	@Test
	public void testAddEvent() throws Exception {
		when(eventService.findOne(0)).thenReturn(null);
		when(venueService.findOne(venue.getId())).thenReturn(venue);
		when(venueService.findAll()).thenReturn((Iterable<Venue>) Arrays.asList(venue));
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String date = format.format(new Date(new Date().getTime() + 3600*48*1000));
		String date1 = format.format(new Date(new Date().getTime() - 3600*48*1000));
		
		// get request
		mvc.perform(get("/events/add").accept(MediaType.TEXT_HTML))
		    .andExpect(status().is(200)).andExpect(view().name("events/add"));
		
		// accepted
		mvc.perform(post("/events/add").contentType(MediaType.APPLICATION_FORM_URLENCODED)
		    .accept(MediaType.TEXT_HTML)
		    .param("name", "Test Event 2")
		    .param("description", "Test description")
		    .param("venue", "" + venue.getId())
		    .param("date", date)
		    .param("time", "00:00:00"))
		    .andExpect(status().is(302)).andExpect(view().name("redirect:/events/0"));
		
		// no name
		mvc.perform(post("/events/add").contentType(MediaType.APPLICATION_FORM_URLENCODED)
		    .accept(MediaType.TEXT_HTML)
		    .param("name", "")
		    .param("description", "Test description")
		    .param("venue", "" + venue.getId())
		    .param("date", date)
		    .param("time", "00:00:00"))
		    .andExpect(status().is(200)).andExpect(view().name("events/add"));
		
		// no venue
		mvc.perform(post("/events/add").contentType(MediaType.APPLICATION_FORM_URLENCODED)
		    .accept(MediaType.TEXT_HTML)
		    .param("name", "Test Event 2")
		    .param("description", "Test description")
		    .param("venue", "")
		    .param("date", date)
		    .param("time", "00:00:00"))
		    .andDo(print())
		    .andExpect(status().is(200)).andExpect(view().name("events/add"));
		
		// no description
		mvc.perform(post("/events/add").contentType(MediaType.APPLICATION_FORM_URLENCODED)
		    .accept(MediaType.TEXT_HTML)
		    .param("name", "Test Event 2")
		    .param("description", "")
		    .param("venue", "" + venue.getId())
		    .param("date", date)
		    .param("time", "00:00:00"))
		    .andExpect(status().is(200)).andExpect(view().name("events/add"));
		
		// no date
		mvc.perform(post("/events/add").contentType(MediaType.APPLICATION_FORM_URLENCODED)
		    .accept(MediaType.TEXT_HTML)
		    .param("name", "Test Event 2")
		    .param("description", "Test description")
		    .param("venue", "" + venue.getId())
		    .param("date", "")
		    .param("time", "00:00:00"))
		    .andExpect(status().is(200)).andExpect(view().name("events/add"));
		
		// no time
		mvc.perform(post("/events/add").contentType(MediaType.APPLICATION_FORM_URLENCODED)
		    .accept(MediaType.TEXT_HTML)
		    .param("name", "Test Event 2")
		    .param("description", "Test description")
		    .param("venue", "" + venue.getId())
		    .param("date", date)
		    .param("time", ""))
		    .andExpect(status().is(200)).andExpect(view().name("events/add"));
		
		// past date
		mvc.perform(post("/events/add").contentType(MediaType.APPLICATION_FORM_URLENCODED)
		    .accept(MediaType.TEXT_HTML)
		    .param("name", "Test Event 2")
		    .param("description", "Test description")
		    .param("venue", "" + venue.getId())
		    .param("date", date1)
		    .param("time", "00:00:00"))
		    .andExpect(status().is(200)).andExpect(view().name("events/add"));
	}
	
	@Test
	public void TwitterPostTweetTest() throws Exception {
		Event event0 = new Event();
		event0.setVenue(venue);
		when(eventService.findOne(0)).thenReturn(event0);
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String date = format.format(new Date(new Date().getTime() + 3600*48*1000));

		// ADD EVENT TO GET AN EVENT DETAIL PAGE
		mvc.perform(post("/events/add").contentType(MediaType.APPLICATION_FORM_URLENCODED)
			    .accept(MediaType.TEXT_HTML)
			    .param("name", "Test Event 2")
			    .param("description", "Test description")
			    .param("venue", "" + venue.getId())
			    .param("date", date)
			    .param("time", "00:00:00"));

		
		mvc.perform(get("/events/0").accept(MediaType.TEXT_HTML)).andExpect(status().is(200))
        .andExpect(view().name("events/details"));
		
		when(connectionRepo.findPrimaryConnection(Twitter.class)).thenReturn(null);
		
		// SUCCESSFUL TWEET WHEN LOGGED IN
		mvc.perform(post("/events/0").contentType(MediaType.APPLICATION_FORM_URLENCODED)
			    .accept(MediaType.TEXT_HTML)
			    .param("tweet", String.valueOf(Math.random() * 2000000)))
	    .andExpect(status().is(302)).andExpect(view().name("redirect:/connect/twitter"));		
		
		// UNSUCCESSFUL TWEET WHEN NOT LOGGED IN
		when(connectionRepo.findPrimaryConnection(Twitter.class)).thenReturn(twitterConn);

		mvc.perform(post("/events/0").contentType(MediaType.APPLICATION_FORM_URLENCODED)
			    .accept(MediaType.TEXT_HTML)
			    .param("tweet", String.valueOf(Math.random() * 2000000)))
			    .andExpect(status().is(200)).andExpect(view().name("events/details"));
	}
}