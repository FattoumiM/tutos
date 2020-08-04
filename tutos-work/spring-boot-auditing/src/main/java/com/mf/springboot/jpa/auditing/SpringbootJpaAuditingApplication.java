package com.mf.springboot.jpa.auditing;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.mf.springboot.jpa.auditing.audit.AuditorAwareImpl;
import com.mf.springboot.jpa.auditing.model.User;
import com.mf.springboot.jpa.auditing.repository.UserRepository;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class SpringbootJpaAuditingApplication {

	@Bean
	public AuditorAware<String> auditorAware() {
		return new AuditorAwareImpl();
	}

	@Bean
    public CommandLineRunner auditingDemo(UserRepository userRepository) {
        return args -> {

            // create new todos
        	userRepository.save(new User("Maher"));
        	userRepository.save(new User("Dalinda"));
        	
//        	userRepository.saveAll(Arrays.asList(
//                    new User("Maher"),
//                    new User("Dalinda")
//            ));

            // retrieve all todos
            Iterable<User> users = userRepository.findAll();

            // print all todos
            users.forEach(System.out::println);
        };
    }
	
	public static void main(String[] args) {
		SpringApplication.run(SpringbootJpaAuditingApplication.class, args);
	}
}
