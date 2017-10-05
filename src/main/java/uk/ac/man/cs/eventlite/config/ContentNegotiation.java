package uk.ac.man.cs.eventlite.config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Handlebars.Utils;
import com.github.jknack.handlebars.Handlebars.SafeString;
import com.github.jknack.handlebars.springmvc.HandlebarsViewResolver;

import pl.allegro.tech.boot.autoconfigure.handlebars.HandlebarsProperties;

@Configuration
public class ContentNegotiation extends WebMvcConfigurerAdapter 
{
	@Autowired
	private HandlebarsProperties handlebars;

	@Autowired
	private Environment environment;

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) 
	{
		configurer.favorPathExtension(false).favorParameter(false).ignoreAcceptHeader(false).useJaf(false)
		.defaultContentType(MediaType.TEXT_HTML).mediaType("html", MediaType.TEXT_HTML)
		.mediaType("json", MediaType.APPLICATION_JSON);
	} // configureContentNegotiation

	@Bean
	public ViewResolver contentNegotiatingViewResolver(ContentNegotiationManager manager) 
	{
		List<ViewResolver> resolvers = new ArrayList<ViewResolver>();
		resolvers.add(getHtmlTemplateViewResolver());
		resolvers.add(getJsonTemplateViewResolver());
		resolvers.add(new JsonViewResolver(isDefaultProfile()));

		ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
		resolver.setContentNegotiationManager(manager);
		resolver.setViewResolvers(resolvers);

		return resolver;
	} // contentNegotiatingViewResolver

	private ViewResolver getHtmlTemplateViewResolver() 
	{
		HandlebarsViewResolver resolver = new HandlebarsViewResolver();
		handlebars.applyToViewResolver(resolver);
		resolver.setSuffix(".html.hbs");
		resolver.setContentType(MediaType.TEXT_HTML_VALUE);
		resolver.setCache(!isDefaultProfile());
		resolver.setOrder(1);

		resolver.registerHelper("paragraphs", new Helper<String>() 
		{
			public CharSequence apply(String desc, Options opts) 
			{
				String[] lines = desc.split("\\r?\\n");
				String paragraphs = "";

				for (int i = 0; i < lines.length; i++) 
				{
					paragraphs += "<p>" + Utils.escapeExpression(lines[i]) + "</p>";
				} // for

				return new SafeString(paragraphs);
			} // apply
		});
		
		resolver.registerHelper("dateFormat", new Helper<Date>() 
				{
					public CharSequence apply(Date date, Options opts) 
					{
						if (opts.param(0).equals("time")) {
							SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
							return format.format(date);
						} else if (opts.param(0).equals("date")) {
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
							return format.format(date);
						} else {
							return null;
						}
					} // apply
				});
		return resolver;
		
	} // getHtmlTemplateViewResolver

	private ViewResolver getJsonTemplateViewResolver() 
	{
		HandlebarsViewResolver resolver = new HandlebarsViewResolver();
		handlebars.applyToViewResolver(resolver);
		resolver.setSuffix(".json.hbs");
		resolver.setContentType(MediaType.APPLICATION_JSON_VALUE);
		resolver.setCache(!isDefaultProfile());
		resolver.setOrder(2);

		return resolver;
	} // getJsonTemplateViewResolver

	private boolean isDefaultProfile() 
	{
		String[] profiles = environment.getActiveProfiles();

		return (profiles.length == 0) || Arrays.asList(environment.getActiveProfiles()).contains("default");
		
	} // isDefaultProfile
	
} // ContentNegotiation Class
