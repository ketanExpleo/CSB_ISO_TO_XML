package com.fss.aeps.test;

import java.io.IOException;
import java.util.Properties;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.fss.aeps.AppConfig;

public class ChangePropertyTest {

	public static void test(ConfigurableApplicationContext context) throws IOException {
		System.out.println("original orgId : "+context.getEnvironment().getProperty("orgId"));
		final ConfigurableEnvironment environment = context.getEnvironment();
		final MutablePropertySources propertySources = environment.getPropertySources();
		propertySources.forEach(propertySource -> {
			System.out.println(propertySource.getName());
		});
		propertySources.remove("application");
		propertySources.forEach(propertySource -> {
			System.out.println(propertySource.getName());
		});
		final Resource resource = new ClassPathResource("/application.properties");
		final Properties properties = PropertiesLoaderUtils.loadProperties(resource);
		properties.put("orgId", "123456");
		PropertySource<?> source = new PropertiesPropertySource("app", properties);
		propertySources.addFirst(source);
		System.out.println("changed orgId : "+context.getEnvironment().getProperty("orgId"));
		System.out.println("changed orgId : "+context.getBean(AppConfig.class).orgId);
	}


}
