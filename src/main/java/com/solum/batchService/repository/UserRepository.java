package com.solum.batchService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solum.batchService.entity.BatchUser;

public interface UserRepository extends JpaRepository<BatchUser,Integer>{

}
