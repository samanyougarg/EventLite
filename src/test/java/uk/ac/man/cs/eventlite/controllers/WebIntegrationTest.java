package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

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
import org.springframework.test.web.servlet.MockMvc;

import uk.ac.man.cs.eventlite.TestParent;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebIntegrationTest extends TestParent {


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
		headers.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
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
	public void testSearchEvents() {
		assertNotNull(event);
		
		String term = event.getName().substring(event.getName().indexOf(' '));
		
		get("/events/s/?name=" + term);
	}

	@Test
	public void testEventAdd() {
		get("/events/add");
	}

	@Test
	public void testEventAddPost() throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String date = format.format(new Date(new Date().getTime() + 3600*48*1000));
		String body = "name=Test+Event+1&description=Test&venue=1&date=" + date + "&time=00:00:00";
		String text = "Test Event 1";
		
		post("/events/add", body, text);
	}

	@Test
	public void testEventDetails() {
		assertNotNull(event);
		
		get("/events/" + event.getId());
	}

	@Test
	public void testEventUpdate() {
		assertNotNull(event);
		
		get("/events/" + event.getId() + "/update");
	}
	
	@Test
	public void testSearchVenue1() {
		getString("/venues/s/?name=Tiger", "Tiger");
	}
	
	@Test
	public void testSearchVenue2() {
		getString("/venues/s/?name=Sound", "Sound Control");
	}
	
	@Test
	public void testSearchVenue3() {
		getString("/venues/s/?name=Fifth", "Fifth NightClub");
	}
	
	
	@Test
	public void testEventUpdatePost() throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String date = format.format(new Date(new Date().getTime() + 3600*48*1000));
		String body = "name=Test+Event+2&description=Test&venue=" + venue.getId() + "&date=" + date + "&time=00:00:00";
		String text = "Test Event 2";
		
		post("/events/" + event.getId() + "/update", body, text);
	}

	@Test
	public void testVenues() {
		get("/venues");
	}
	
	@Test
	public void testVenueAdd() {
		get("/venues/add");
	}
	
	@Test
	public void testVenueAddPost() throws Exception {
		String body = "name=Test+Venue+1&capacity=10&address=kilburn+building&city=manchester&zip=m139pl";
		String text = "Test Venue 1";
		
		post("/venues/add", body, text);
	}
	
	@Test
	public void testVenueDetails() {
		get("/venues/" + venue.getId());
	}
	
	@Test
	public void testVenueUpdate() {
		get("/venues/" + venue.getId() + "/update");
	}
	
	@Test
	public void testVenueUpdatePost() throws Exception {
		String body = "name=Test+Venue+2&capacity=10&address=kilburn+building&city=manchester&zip=m139pl";
		String text = "Test Venue 2";
		
		post("/venues/" + venue.getId() + "/update", body, text);
	}
	
	private void get(String url) {
		ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, httpEntity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getHeaders().getContentType().toString(), containsString(MediaType.TEXT_HTML_VALUE));
	}

	private void postString(String url, String body, String expectedText) throws Exception {
		HttpHeaders postHeaders = new HttpHeaders();
 		postHeaders.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
 		postHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
 		HttpEntity<String> postEntity = new HttpEntity<String>(body, postHeaders);
 
 		ResponseEntity<String> response = template.exchange(url, HttpMethod.POST, postEntity, String.class);
 		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
 		assertThat(response.getBody(), containsString(expectedText));
	}
	
	private void getString(String url, String expectedBody) {
		
		ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, httpEntity, String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getHeaders().getContentType().toString(), containsString(MediaType.TEXT_HTML_VALUE));
	
		
		
//		ResponseEntity<String> response = template.exchange(url,
//				HttpMethod.GET, httpEntity, String.class);
//		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
//		assertThat(response.getHeaders().getContentType().toString(),
//				containsString(MediaType.TEXT_HTML_VALUE));
		assertThat(response.getBody(), containsString(expectedBody));
	}
}
