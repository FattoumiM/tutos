package com.mf.springboot.jpa.auditing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mf.springboot.jpa.auditing.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

}
