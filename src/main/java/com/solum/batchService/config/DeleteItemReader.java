package com.solum.batchService.config;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;

import com.solum.batchService.entity.BatchUser;
import com.solum.batchService.repository.UserRepository;

import java.util.Iterator;

public class DeleteItemReader implements ItemReader<BatchUser> {

    @Autowired
    private UserRepository userRepository;

    private Iterator<BatchUser> iterator;

    @Override
    public BatchUser read() {
        if (iterator != null && iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    public void setItemsToDelete(Iterable<BatchUser> itemsToDelete) {
        this.iterator = itemsToDelete.iterator();
    }
}
