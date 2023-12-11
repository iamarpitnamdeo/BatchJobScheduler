package com.example.userBatchService.service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import com.example.userBatchService.model.BatchUser;
import com.example.userBatchService.repository.UserRepository;

import jakarta.annotation.PostConstruct;

@Service
public class UserService {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TaskScheduler taskScheduler;
	

	
	public BatchUser addUser(BatchUser user) {		
		return userRepository.save(user);
	}
	
	public void deleteUsersInBatch(int batchSize) {

		LOGGER.info("Get a list of users to delete");
	    List<BatchUser> usersToDelete = getUserListForDeletion(batchSize);
	    
 
	    LOGGER.info("Delete users in batches");
	    if (!usersToDelete.isEmpty()) {
	        do {
	        	LOGGER.info("Delete a batch of users");
	            deleteUsers(usersToDelete);
	            
	 
	            LOGGER.info("Update the list of users to delete");
	            usersToDelete = getUserListForDeletion(batchSize);
	        } while (!usersToDelete.isEmpty());
	    }
	}

	
	
	
	private List<BatchUser> getUserListForDeletion(int batchSize) {
        List<BatchUser> usersToDelete = new ArrayList<>();
        for (int i = 0; i < userRepository.count()  && i < batchSize; i++) {
            usersToDelete.add(userRepository.findAll().get(i));
        }
        return usersToDelete;
    }

    private void deleteUsers(List<BatchUser> usersToDelete) {
        userRepository.deleteAll(usersToDelete);
        System.out.println("Deleted users: " + usersToDelete);
    }
    
	@PostConstruct
	public void scheduleDeletion() { 
	    Instant startTime = Instant.now();
	    LOGGER.info("Started scheduler to delete logs for every 10 seconds");
		taskScheduler.scheduleAtFixedRate(() -> {
		    deleteUsersInBatch(10);
		}, startTime, Duration.ofSeconds(10));
		
	}
	
	

}
