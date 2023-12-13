package com.solum.batchService.controller;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.solum.batchService.entity.BatchUser;
import com.solum.batchService.repository.UserRepository;

@RestController
@RequestMapping("/jobs")
public class JobController {

    @Autowired
    private JobLauncher jobLauncher;
    
	@Autowired
	private TaskScheduler taskScheduler;
	
	@Autowired
	private UserRepository userRepository;

    @Autowired
    @Qualifier("runJob") // Specify the qualifier for the runJob bean
    private Job runJob;

    @Autowired
    @Qualifier("deleteJob") // Specify the qualifier for the deleteJob bean
    private Job deleteJob;
    
    
    @PostMapping("/importUsers")
    public void importCsvToDBJob() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis()).toJobParameters();
        try {
            jobLauncher.run(runJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            e.printStackTrace();
        }
    }
    
    @DeleteMapping("/deleteUsers")
    public void deleteUsersFromDB() {
    	JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis()).toJobParameters();
        try {
            jobLauncher.run(deleteJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            e.printStackTrace();
        }
    }
    
	public void deleteUsersInBatch(int batchSize) {

	    List<BatchUser> usersToDelete = getUserListForDeletion(batchSize);
	    
	    if (!usersToDelete.isEmpty()) {
	        do {
	            deleteUsers(usersToDelete);
	            usersToDelete = getUserListForDeletion(batchSize);
	        } while (!usersToDelete.isEmpty());
	    }
	}
	   private void deleteUsers(List<BatchUser> usersToDelete) {
	        userRepository.deleteAll(usersToDelete);
	        System.out.println("Deleted users: " + usersToDelete);
	    }
	
	private List<BatchUser> getUserListForDeletion(int batchSize) {
        List<BatchUser> usersToDelete = new ArrayList<>();
        for (int i = 0; i < userRepository.count()  && i < batchSize; i++) {
            usersToDelete.add(userRepository.findAll().get(i));
        }
        return usersToDelete;
    }
	
	@PostConstruct
	public void scheduleDeletion() { 
	    Instant startTime = Instant.now();
		taskScheduler.scheduleAtFixedRate(() -> {
		    deleteUsersInBatch(10);
		}, startTime, Duration.ofSeconds(20));
		
	}
}
