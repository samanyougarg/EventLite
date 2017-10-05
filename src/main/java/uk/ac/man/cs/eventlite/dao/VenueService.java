package uk.ac.man.cs.eventlite.dao;

import uk.ac.man.cs.eventlite.entities.Event;
import java.util.List;
import uk.ac.man.cs.eventlite.entities.Venue;

@SuppressWarnings("unused")
public interface VenueService 
{
	public long count();

	public Venue save(Venue venue);
	
	public void update(Venue venue);

	public Iterable<Venue> findAll();
	
	public Venue findOne(long id);
	
	public Iterable<Venue> findByNameIgnoreCaseContainingOrderByNameAsc(String name);

	public void add(Venue venue);

	public List<Venue> findTopVenues();
		
	public void delete(long id);
	
	public int countEvents(long id);
	
} // VenueService Class

