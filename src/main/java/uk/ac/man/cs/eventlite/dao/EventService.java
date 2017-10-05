package uk.ac.man.cs.eventlite.dao;

import java.util.List;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

public interface EventService 
{
	public long count();

	public Event save(Event event);
	
	public void update(Event event);

	public Iterable<Event> findAll();
	
	public Iterable<Event> findAllByVenue(Venue venue);
	
	public Event findOne(long id);
	
	public List<Event> findAllByOrderByDateDesc();
	
	public void delete(Event event);
	
	public void delete(long id);
	
	public boolean exists(long id);

	public Iterable<Event> findByNameIgnoreCaseContainingOrderByDateDescNameAsc(String name);
	
	public void add(Event event);

	public List<Event> findUpcomingEvents(boolean forSearch);
	
	public List<Event> findPastEvents(boolean forSearch);

} // EventService Class