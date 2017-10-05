package uk.ac.man.cs.eventlite.config;

import java.util.Locale;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

public class JsonViewResolver implements ViewResolver
{
	private boolean development;

	public JsonViewResolver(boolean development) 
	{
		this.development = development;
	} // JsonViewResolver

	public View resolveViewName(String viewName, Locale locale) throws Exception 
	{
		MappingJackson2JsonView view = new MappingJackson2JsonView();
		view.setPrettyPrint(development);

		return view;
	} // resolveViewName
	
} // JsonViewResolver Class
