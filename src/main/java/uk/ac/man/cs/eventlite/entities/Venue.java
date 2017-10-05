package uk.ac.man.cs.eventlite.entities;

import javax.persistence.*;

@Entity
@Table(name = "venues")

public class Venue 
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String name;
	private int capacity;
	private String address;
	private String city; 
	private String zip;
	private String lat;
	private String lng;

	public Venue() {}

	public long getId() 
	{
		return id;
	} // getId

	public void setId(long id) 
	{
		this.id = id;
	} // setId

	public String getName() 
	{
		return name;
	} // getName

	public void setName(String name) 
	{
		this.name = name;
	} // setName

	public int getCapacity() 
	{
		return capacity;
	} // getCapacity

	public void setCapacity(int capacity) 
	{
		this.capacity = capacity;
	} // setCapacity

	public String getLat() 
	{
		return lat;
	} // getLat

	public void setLat(String lat) 
	{
		this.lat = lat;
	} // setLat

	public String getLng() 
	{
		return lng;
	} // getLng

	public void setLng(String lng) 
	{
		this.lng = lng;
	} // setLng

	public String getAddress() 
	{
		return address;
	} // getAddress

	public void setAddress(String address) 
	{
		this.address = address;
	} // setAddress

	public String getZip() 
	{
		return zip;
	} // getZip

	public void setZip(String zip) 
	{
		this.zip = zip;
	} // setZip
	
	public String getCity() 
	{
		return city;
	} // getCity

	public void setCity(String city) 
	{
		this.city = city;
	} // setCity

	public static String validate(Venue venue) 
	{
		if (venue.name == null || venue.name.length() == 0 || venue.name.length() > 255) 
		{
			return "Venue name is invalid.";
		} // if
		
		if (venue.city == null || venue.city.length() == 0 || venue.city.length() > 100) 
		{
			return "Venue city is invalid.";
		} // if
		
		if (venue.address == null || venue.address.length() == 0 || venue.address.length() > 299) 
		{
			return "Venue address is invalid.";
		} // if
		
		if (venue.capacity < 1) 
		{
			return "Venue capacity is invalid.";
		} // if

		if (venue.zip == null || venue.zip.length() == 0 || venue.zip.length() > 7 || venue.zip.length() < 4 ) 
		{
			return "Venue postcode is invalid.";
		} // if

		return "";
	} // validate

}
