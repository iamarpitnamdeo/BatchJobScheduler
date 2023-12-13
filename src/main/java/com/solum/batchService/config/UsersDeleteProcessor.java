package com.solum.batchService.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.solum.batchService.entity.BatchUser;
import com.solum.batchService.repository.UserRepository;

public class UsersDeleteProcessor implements ItemProcessor<BatchUser,BatchUser> {

    private static final Logger log = LoggerFactory.getLogger(UsersDeleteProcessor.class);


    private final UserRepository userRepository;

    public UsersDeleteProcessor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public BatchUser process(BatchUser user) throws Exception {
        // Log before deletion
        log.info("Deleting record with id: {}", user.getId());

        // Delete the record from the database
        userRepository.delete(user);

        // Log after deletion
        log.info("Record with id {} deleted successfully", user.getId());

        // Return null to indicate that the item has been processed
        return null;
    }
    
	
}
