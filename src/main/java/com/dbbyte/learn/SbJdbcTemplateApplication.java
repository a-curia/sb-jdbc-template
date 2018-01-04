package com.dbbyte.learn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.dbbyte.learn.model.Student;
import com.dbbyte.learn.repository.StudentJdbcRepository;

@SpringBootApplication
public class SbJdbcTemplateApplication implements CommandLineRunner {

	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	StudentJdbcRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(SbJdbcTemplateApplication.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {

		logger.info("Student id 10001 -> {}", repository.findById(10001L));
		logger.info("All users 1 -> {}", repository.findAll());
		
		logger.info("Inserting -> {}", repository.insert(new Student(10010L, "ionica", "A1234657")));

		logger.info("Update 10001 -> {}", repository.update(new Student(10001L, "Adrian-Updated", "Adrian-new-Passport")));

		repository.deleteById(10002L);

		logger.info("All users 2 -> {}", repository.findAll());
	}
}
