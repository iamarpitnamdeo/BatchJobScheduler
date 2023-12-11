package com.example.userBatchService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.userBatchService.model.BatchUser;

public interface UserRepository extends JpaRepository<BatchUser,Long>{

}
