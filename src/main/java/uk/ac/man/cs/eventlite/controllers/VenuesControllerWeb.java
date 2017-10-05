package uk.ac.man.cs.eventlite.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@SuppressWarnings("unused")
@Controller
@RequestMapping("/venues")
public class VenuesControllerWeb 
{
	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;

	// SEARCHING VENUES FUNCTION \\ 
	
	@RequestMapping(method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
	public String getAllVenues(Model model) 
	{
		model.addAttribute("venues", venueService.findAll());
		return "venues/index";  
	} // getAllVenues

	@RequestMapping(value = "/s", method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
	public String getSearchedVenues(@RequestParam(value = "name", required = true) String name, Model model) 
	{
		if (name != "") 
		{
			Iterable<Venue> venueListIterable = venueService.findByNameIgnoreCaseContainingOrderByNameAsc(name);

			Iterator<Venue> venueList = venueListIterable.iterator();
			Pattern pattern = Pattern.compile("(?i)\\b" + name + "\\b");

			while (venueList.hasNext()) 
			{
				Venue venue = venueList.next();
				
				Matcher matcher = pattern.matcher(venue.getName());
				
				if (!(matcher.find()))
					venueList.remove();
			} // while
			model.addAttribute("venues", venueListIterable);
		}  // if
		else 
		{
			model.addAttribute("venues", venueService.findAll());
		} // else
		return "venues/index";
	} // getSearchedVenues

	// VENUE DETAILS RETURNING \\
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
	public String getVenueDetails(@PathVariable(value = "id") long id, Model model) 
	{
		Venue venue = venueService.findOne(id);

		if (venue == null) 
		{
			return "redirect:/venues";
		} // if

		List<Event> events = eventService.findAllByOrderByDateDesc();
		List<Event> eventsupcoming = new ArrayList<Event>();
		List<Event> eventspast = new ArrayList<Event>();
		  
		Date date = new Date();    
		  
		for (Event index : events)
		{
		  if (index.getVenue().getId() == id)
		  {
		    if (index.getDate().after(date))
		      eventsupcoming.add(index);
		    if (index.getDate().before(date))
		      eventspast.add(index);          
		  }
		}
		
		model.addAttribute("eventsupcoming", eventsupcoming);
		model.addAttribute("eventspast", eventspast);
		model.addAttribute("venue", venue);
		

		//START GMAPS
 	    List<Map<String, String>> markers = new ArrayList<Map<String, String>>();
 	    
 	    Map<String, String> center = new HashMap<String, String>();
 	    
 	    center.put("lat", venue.getLat());
 	    center.put("lng", venue.getLng());
 	    center.put("label", venue.getName());
 	    
 	    markers.add(center);
 	    
 	    model.addAttribute("markers", markers);
		//END GMAPS
 	    
		return "venues/details";
	} // getVenueDetails

	// UPDATING THE VENUE FUNCTION \\
	
	@RequestMapping(value = "/{id}/update", method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
	public String updateVenue(@PathVariable("id") long id, Model model) 
	{
		Venue venue = venueService.findOne(id);

		if (venue != null) 
		{ 
			model.addAttribute("venue", venue);

			return "venues/update";
		} // if

		return "redirect:/venues";
	} // updateVenue

	@RequestMapping(value = "/{id}/update", method = RequestMethod.POST, produces = { MediaType.TEXT_HTML_VALUE })
	public String updateVenue(@PathVariable("id") long id,
	                          @RequestParam("name") String name,
	                          @RequestParam("capacity") String capacity,
	                          @RequestParam("address") String address,
	                          @RequestParam("city") String city,
	                          @RequestParam("zip") String zip,
	                          Model model) 
	{
		Venue venue = venueService.findOne(id);
		venue.setName(name);
		System.err.println(name);
		System.err.println(venue.getName());
		venue.setAddress(address);
		venue.setCity(city);
		venue.setZip(zip);
		
		try {
			venue.setCapacity(Integer.valueOf(capacity));
		} catch (Exception e) {
			venue.setCapacity(-1);
		}
		
		String error = Venue.validate(venue);
		
		if (error.length() > 0) 
		{
			model.addAttribute("venue", venueService.findOne(id));
			model.addAttribute("error", error);

			return "venues/update";
		} // if
		
		// Get matching lat and long of venue
		GeoApiContext gaContext = new GeoApiContext().setApiKey("AIzaSyAraV9DAJsEGnB1HaDhorxkMGIbKQgZTFQ");
		try 
		{
			GeocodingResult[] result = GeocodingApi.geocode(gaContext, venue.getAddress() +" " + venue.getCity() + " " + venue.getZip())
					.await();
			Geometry latLong = result[0].geometry;
			LatLng location = latLong.location;
			venue.setLat(String.valueOf(location.lat));
			venue.setLng(String.valueOf(location.lng));
		} // try
		catch (Exception e) 
		{
		}// catch
		
		venueService.update(venue); 
		
		return "redirect:/venues/" + id; 
	} // updateVenue

	// ADDING VENUE FUNCTION \\
	
	@RequestMapping(method = RequestMethod.GET, value = "/add", produces = { MediaType.TEXT_HTML_VALUE })
	public String addPage(Model model) 
	{
		return "venues/add";
	} // addPage

	@RequestMapping(method = RequestMethod.POST, value = "/add", produces = { MediaType.TEXT_HTML_VALUE })
	public String addVenue(@RequestParam("name") String name,
                           @RequestParam("capacity") String capacity,
                           @RequestParam("address") String address,
                           @RequestParam("city") String city,
                           @RequestParam("zip") String zip,
                           Model model) 
	{
		Venue venue = new Venue();
		venue.setName(name);
		venue.setAddress(address);
		venue.setCity(city);
		venue.setZip(zip);
		
		try {
			venue.setCapacity(Integer.valueOf(capacity));
		} catch (Exception e) {
			venue.setCapacity(-1);
		}
		
		String error = Venue.validate(venue);
		System.out.println(error);
		
		if (error.length() > 0) 
		{
			model.addAttribute("venue", venue);
			model.addAttribute("error", error);
			return "venues/add";
		} // if
		
		// Get matching lat and long of venue
		GeoApiContext gaContext = new GeoApiContext().setApiKey("AIzaSyAraV9DAJsEGnB1HaDhorxkMGIbKQgZTFQ");
		try 
		{
			GeocodingResult[] result = GeocodingApi.geocode(gaContext, venue.getAddress() +" " + venue.getCity() + " " + venue.getZip())
					.await();
			Geometry latLong = result[0].geometry;
			LatLng location = latLong.location;
			venue.setLat(String.valueOf(location.lat));
			venue.setLng(String.valueOf(location.lng));
		} // try
		catch (Exception e) 
		{
		}// catch

		venueService.add(venue);
		
		return "redirect:/venues/" + venue.getId();
	} // addVenue
	
	// DELETING THE VENUE FUNCTION \\
	
	@RequestMapping(value = "/{id}/delete", method = RequestMethod.GET, produces = { MediaType.TEXT_HTML_VALUE })
	public String deleteVenueWeb(@PathVariable("id") long id, Model model) 
	{
		if (venueService.countEvents(id) > 0) {
			model.addAttribute("error", "Cannot delete venue that is hosting events.");
			model.addAttribute("venue", venueService.findOne(id));
			
			return "venues/details";
		}
		
		venueService.delete(id);
		return "redirect:/venues";
	} // deleteVenueWeb
	@RequestMapping(value = "/{id}/delete", method = RequestMethod.DELETE)
	public @ResponseBody String deleteVenueApi(@PathVariable("id") long id) 
	{
		venueService.delete(id);

		return "/venues";
	} // deleteVenueApi

} // VenuesControllerWeb Class