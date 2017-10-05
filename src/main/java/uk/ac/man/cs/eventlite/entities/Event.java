package uk.ac.man.cs.eventlite.entities;

import java.util.Date;
import javax.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name="events")
public class Event 
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Temporal(TemporalType.DATE)
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date date;

	@Temporal(TemporalType.TIME)
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@DateTimeFormat(pattern = "HH:mm:ss")
	private Date time;

	private String name;
	private String description;

	@ManyToOne
	private Venue venue;

	public Event() {}

	public long getId() 
	{
		return id;
	} // getId

	public void setId(long id) 
	{
		this.id = id;
	} // setId

	public Date getDate() 
	{
		return date;
	} // getDate

	public void setDate(Date date) 
	{
		this.date = date;
	} // setDate

	public Date getTime() 
	{
		return time;
	} // getTime

	public void setTime(Date time) 
	{
		this.time = time;
	} // setTime

	public String getName() 
	{
		return name;
	} // getName

	public void setName(String name) 
	{
		this.name = name;
	} // setName
	
	public String getDescription() 
	{
		return description;
	} // getDescription

	public void setDescription(String description) 
	{
		this.description = description;
	} // setDescription

	public Venue getVenue() 
	{
		return venue;
	} // getVenue

	public void setVenue(Venue venue) 
	{
		this.venue = venue;
	} // setVenue
	
	public static String validate(Event event) 
	{
		if (event.name == null || event.name.length() == 0 || event.name.length() > 255) 
		{
			return "Event name is invalid.";
		} // if

		if (event.venue == null) 
		{
			return "Event venue is invalid.";
		} // if

		if (event.description == null || event.description.length() == 0 || event.description.length() > 500) 
		{
			return "Event description is invalid.";
		} // if

		if (event.date == null) 
		{
			return "Event must have a date.";
		} // if
		
		if (event.time == null) 
		{
			return "Event must have a time.";
		} // if

		if (event.date.before(new Date())) 
		{
			return "Event must happen in the future.";
		} // if

		return "";
	} // validate
	
} // Event Class
