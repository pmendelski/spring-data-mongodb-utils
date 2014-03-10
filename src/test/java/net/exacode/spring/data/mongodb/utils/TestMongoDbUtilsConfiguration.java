package net.exacode.spring.data.mongodb.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

@Configuration
@Import(MongoDbUtilConfiguration.class)
@PropertySource("classpath:db.properties")
public class TestMongoDbUtilsConfiguration extends AbstractMongoConfiguration {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private Environment environment;

	@Override
	protected String getDatabaseName() {
		return environment.getProperty("db.name");
	}

	@Override
	public Mongo mongo() throws Exception {
		String host = environment.getProperty("db.host");
		int port = environment.getProperty("db.port", Integer.class);
		logger.info("Connecting to mongodb: {}:{}/{}", host, port,
				getDatabaseName());
		return new MongoClient(host, port);
	}

	@Override
	protected UserCredentials getUserCredentials() {
		String username = environment.getProperty("db.username");
		String password = environment.getProperty("db.password");
		if (username != null && username.length() > 0) {
			return new UserCredentials(username, password);
		}
		return super.getUserCredentials();
	}

	@Override
	@Bean
	public MongoTemplate mongoTemplate() throws Exception {
		MongoTemplate template = new MongoTemplate(mongoDbFactory(),
				mappingMongoConverter());
		template.setWriteResultChecking(WriteResultChecking.EXCEPTION);
		return template;
	}

	@Bean
	public GridFsTemplate gridFsTemplate() throws Exception {
		return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
	}

}
