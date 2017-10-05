package uk.ac.man.cs.eventlite.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@SuppressWarnings("unused")
public interface VenueRepository extends CrudRepository<Venue, Long>
{
	public Iterable<Venue> findByNameIgnoreCaseContainingOrderByNameAsc(String name);

    @Query("select v from Venue v, Event e where v.id = e.venue group by v.id order by count(e.id) desc")
    public List<Venue> findTopVenues(Pageable limit);
    
    @Query("select count(e.id) from Venue v, Event e where v.id = :vid and e.venue = :vid")
    public int countEvents(@Param("vid") long id);
        
} // VenueRepository Class

