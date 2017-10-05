package uk.ac.man.cs.eventlite.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

public interface EventRepository extends CrudRepository<Event, Long> 
{
	public List<Event> findAllByOrderByDateDesc();

	public Iterable<Event> findByNameIgnoreCaseContainingOrderByDateDescNameAsc(String name);
	
	public List<Event> findAllByNameIgnoreCaseContainingOrderByNameAsc(String name);

	public List<Event> findAllByDateAfterOrderByDate(Date date, Pageable limit);
	
	public List<Event> findAllByDateBeforeOrderByDate(Date date, Pageable limit);
	
	public Iterable<Event> findAllByVenue(Venue venue);
	
	public List<Event> findAllByDateAfterOrderByDate(Date date);
	
	public List<Event> findAllByDateBeforeOrderByDate(Date date);

} // EventRepository Class