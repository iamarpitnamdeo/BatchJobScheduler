package com.solum.batchService.config;

import org.springframework.batch.item.ItemProcessor;

import com.solum.batchService.entity.BatchUser;



public class UserProcessor implements ItemProcessor<BatchUser,BatchUser> {

	@Override
	public BatchUser process(BatchUser user) throws Exception {
		return user;
	}

}
