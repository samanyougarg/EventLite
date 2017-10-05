package uk.ac.man.cs.eventlite.dao;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;
@SuppressWarnings("unused")

@Service
public class VenueServiceImpl implements VenueService 
{
	@Autowired
	private VenueRepository venueRepository;
	
	@Override
	public Iterable<Venue> findAll() 
	{
		return venueRepository.findAll();
	} // findAll
	
	@Override
	public long count() 
	{
		return venueRepository.count();
	} // count
	
	@Override
	public Venue save(Venue venue)
	{
		return venueRepository.save(venue);
	} // save

	@Override
	public Venue findOne(long id) 
	{
		return venueRepository.findOne(id);
	} // findOne
	
	@Override
	public void update(Venue venue) 
	{
		venueRepository.save(venue);
	} // update
	
	@Override
	public Iterable<Venue> findByNameIgnoreCaseContainingOrderByNameAsc(String name) 
	{
		return venueRepository.findByNameIgnoreCaseContainingOrderByNameAsc(name);
	} // findByNameIgnoreCaseContainingOrderByNameAsc

	@Override
	public void add(Venue venue) 
	{
		venueRepository.save(venue);		
	} // add

	@Override
	public List<Venue> findTopVenues() 
	{
		Pageable limit = new PageRequest(0, 3);

		return venueRepository.findTopVenues(limit);
	} // findTopVenues

	@Override
	public void delete(long id) 
	{
		venueRepository.delete(id);
	} // delete

	@Override
	public int countEvents(long id)
	{
		return venueRepository.countEvents(id);
	}
	
} // VenueServiceImpl Class 

