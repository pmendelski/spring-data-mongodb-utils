package net.exacode.spring.data.mongodb.utils;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

/**
 * Base for all integration tests.
 * <p>
 * Integration tests uses Spring context and MongoDB
 * 
 * @author mendlik
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestMongoDbUtilsConfiguration.class, loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
public abstract class IntegrationTestBase {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	protected MongoTemplate mongo;

	@Before
	public void before() {
		dropCollections();
	}

	private void dropCollections() {
		for (String collectionName : mongo.getDb().getCollectionNames()) {
			if (!collectionName.startsWith("system.")) {
				mongo.getDb().getCollection(collectionName).drop();
			}
		}
	}

}