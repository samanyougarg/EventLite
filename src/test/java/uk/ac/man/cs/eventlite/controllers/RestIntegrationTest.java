package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import uk.ac.man.cs.eventlite.TestParent;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestIntegrationTest extends TestParent {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate template;
	
	@Autowired
	private EventService eventService;
	
	@Autowired
	private VenueService venueService;
	
	private Event event;
	private Venue venue;

	private HttpEntity<String> httpEntity;
	
	@Before
	public void setup() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		httpEntity = new HttpEntity<String>(headers);
		
		if (event == null || venue == null) {
			event = eventService.findAll().iterator().next();
			venue = venueService.findAll().iterator().next();
		}
	}

	@Test
	public void testHomepage() {
		get("/");
	}
	
	@Test
	public void testEvents() {
		get("/events");
	}

	@Test
	public void testEventDetails() {
		assertNotNull(event);
		
		get("/events/" + event.getId());
	}
	
	@Test
	public void testVenues() {
		get("/venues");
	}
	
	@Test
	public void testVenueDetails() {
		assertNotNull(venue);
		
		get("/venues/" + venue.getId());
	}
	

	private void get(String url) {
		ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, httpEntity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getHeaders().getContentType().toString(), containsString(MediaType.APPLICATION_JSON_VALUE));
	}

}
