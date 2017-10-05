package uk.ac.man.cs.eventlite.dao;

import java.util.List;
import java.util.Date;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@Service
public class EventServiceImpl implements EventService {
	@Autowired
	private EventRepository eventRepository;

	@Override
	public long count() {
		return eventRepository.count();
	} // count

	@Override
	public Iterable<Event> findAll() {
		return eventRepository.findAll();
	} // findAll

	@Override
	public Iterable<Event> findAllByVenue(Venue venue) 
	{
		return eventRepository.findAllByVenue(venue);
	} // findAllByVenue
	
	@Override
	public Event save(Event event)
	{
		return eventRepository.save(event);
	} // save

	@Override
	public List<Event> findAllByOrderByDateDesc() {
		return eventRepository.findAllByOrderByDateDesc();
	} // findAllByOrderByDateDesc

	@Override
	public Event findOne(long id) {
		return eventRepository.findOne(id);
	} // findOne

	@Override
	public void delete(Event event) {
		eventRepository.delete(event);
	} // delete

	@Override
	public void delete(long id) {
		eventRepository.delete(id);
	} // delete

	@Override
	public boolean exists(long id) {
		return eventRepository.exists(id);
	} // exists

	@Override
	public Iterable<Event> findByNameIgnoreCaseContainingOrderByDateDescNameAsc(String name) {
		return eventRepository.findByNameIgnoreCaseContainingOrderByDateDescNameAsc(name);
	} // findByNameIgnoreCaseContainingOrderByDateDescNameAsc

	@Override
	public void update(Event event) {
		eventRepository.save(event);
	} // update

	@Override
	public void add(Event event) {
		eventRepository.save(event);
	} // add

	@Override
	public List<Event> findUpcomingEvents(boolean forSearch) {
		if (!forSearch) {
			Pageable limit = new PageRequest(0, 5);
			return eventRepository.findAllByDateAfterOrderByDate(new Date(), limit);
		} else {
			return eventRepository.findAllByDateAfterOrderByDate(new Date());
		}
	} // findUpcomingEvents

	@Override
	public List<Event> findPastEvents(boolean forSearch) {
		if (!forSearch) {
			Pageable limit = new PageRequest(0, 5);
			return eventRepository.findAllByDateBeforeOrderByDate(new Date(), limit);
		} else {
			return eventRepository.findAllByDateBeforeOrderByDate(new Date());
		}

	}

} // EventServiceImpl Class
