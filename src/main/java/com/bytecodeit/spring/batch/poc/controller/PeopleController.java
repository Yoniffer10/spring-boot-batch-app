package com.bytecodeit.spring.batch.poc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.bytecodeit.spring.batch.poc.model.Person;

@RestController
public class PeopleController {

	private static Logger log = LoggerFactory.getLogger(PeopleController.class);
	
	@Autowired
    JobLauncher jobLauncher;
    
    @Autowired
    Job importUserJob;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @GetMapping("/processUserData")
    @ResponseBody
    List<Person> processDataFromPeople() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException{
        JobExecution jobExecution = jobLauncher.run(importUserJob,createInitialJobParameterMap());
        
        List<Person> list = new ArrayList<Person>();
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {

        	list = jdbcTemplate.query("SELECT person_id, first_name, last_name FROM people",
    				(rs, row) -> new Person(
    					rs.getLong(1),
    					rs.getString(2),
    					rs.getString(3))
    			);
		}
        
        return list;
    }
    private JobParameters createInitialJobParameterMap() {
        Map<String, JobParameter> m = new HashMap<>();
        m.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters p = new JobParameters(m);
        return p;
    }
}
