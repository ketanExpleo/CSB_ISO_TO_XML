package com.fss.aeps;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.fss.aeps.jpa.AepsConfig;
/**
 * @author Krishna Telgave
 * https://www.linkedin.com/in/krishnatelgave8983290664/
 * https://github.com/KrishnaST
 */
@Component
public class ApplicationInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationInitializer.class);

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		try {
			logger.info("ApplicationInitializer initiated.");
			final String profile = System.getProperty("spring.profiles.active");
			final String props = profile == null ? "/application.properties" : "/application-"+profile+".properties";
			final ConfigurableEnvironment environment = applicationContext.getEnvironment();
			final MutablePropertySources propertySources = environment.getPropertySources();
			final Resource resource = new ClassPathResource(props);
			final Properties properties = PropertiesLoaderUtils.loadProperties(resource);
			final DataSource dataSource = getDataSource(properties);
			propertySources.addFirst(getPropertySource(dataSource));
		} catch (Exception e) {throw new RuntimeException("ApplicationContextInitializer Failed", e);}
	}

	private static final PropertySource<?> getPropertySource(final DataSource dataSource) throws IOException {
		logger.info("datasource : "+dataSource.getClass().getName());
		final JdbcTemplate jdbc = new JdbcTemplate(dataSource);
		final List<AepsConfig> configuration = jdbc.query("select PARAM, VALUE, DESCRIPTION from AEPS_CONFIG", (rs, num) -> new AepsConfig(rs.getString(1), rs.getString(2), rs.getString(3)));
		final Map<String, Object> map = new HashMap<>();
		configuration.stream().filter(c -> !c.getParam().startsWith("#")).forEach(c -> map.put(c.getParam(), c.getValue()));
		final MapPropertySource propertySource = new MapPropertySource("application", map);
		logger.info("Configured application properties from: {}", map);
		try {
			if(dataSource instanceof AutoCloseable closeable) {
				closeable.close();
				logger.info("temporary datasource closed.");
			}
		} catch (Exception e) {}
		return propertySource;
	}

	private static final DataSource getDataSource(Properties properties) {
		try {
	        final DataSourceBuilder<?> builder = DataSourceBuilder.create();
	        builder.url(properties.getProperty("spring.datasource.url"));
	        builder.driverClassName(properties.getProperty("spring.datasource.driver-class-name"));
	        builder.username(properties.getProperty("spring.datasource.username"));
	        builder.password(properties.getProperty("spring.datasource.password"));
	        return builder.build();
		} catch (Exception e) {throw new RuntimeException("database initialization failed for property initialization");}
    }
}
