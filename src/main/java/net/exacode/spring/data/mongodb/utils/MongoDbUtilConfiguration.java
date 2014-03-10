package net.exacode.spring.data.mongodb.utils;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Registers all utility components that comes with
 * {@code spring-data-mongodb-utils}.
 * 
 * @author mendlik
 * 
 */
@Configuration
@ComponentScan(basePackageClasses = MongoDbUtilConfiguration.class)
public class MongoDbUtilConfiguration {

}
