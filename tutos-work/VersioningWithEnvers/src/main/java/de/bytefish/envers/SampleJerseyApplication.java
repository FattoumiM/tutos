package de.bytefish.envers;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
public class SampleJerseyApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		new SampleJerseyApplication()
				.configure(new SpringApplicationBuilder(SampleJerseyApplication.class))
//				.properties(getDefaultProperties())
				.run(args);
	}


//	@Bean
//	public DataSource dataSource() {
//
//		HikariDataSource dataSource = new HikariDataSource();
//
//		dataSource.setInitializationFailTimeout(0);
//		dataSource.setMaximumPoolSize(5);
//		dataSource.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");
//		dataSource.addDataSourceProperty("url", "jdbc:postgresql://127.0.0.1:5432/sampledb");
//		dataSource.addDataSourceProperty("user", "root");
//		dataSource.addDataSourceProperty("password", "root");
//
//		return dataSource;
//	}

//	private static Properties getDefaultProperties() {
//
//		Properties defaultProperties = new Properties();
//
//		// Set sane Spring Hibernate properties:
//		defaultProperties.put("spring.jpa.show-sql", "true");
//		defaultProperties.put("spring.jpa.hibernate.naming.physical-strategy", "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
//		defaultProperties.put("spring.datasource.initialize", "false");
//
//		// Store Values on Delete:
//		defaultProperties.put("spring.jpa.properties.org.hibernate.envers.store_data_at_delete", "true");
//
//		// Prevent JPA from trying to Auto Detect the Database:
//		defaultProperties.put("spring.jpa.database", "postgresql");
//
//		// Prevent Hibernate from Automatic Changes to the DDL Schema:
//		defaultProperties.put("spring.jpa.hibernate.ddl-auto", "none");
//
//		return defaultProperties;
//	}

}