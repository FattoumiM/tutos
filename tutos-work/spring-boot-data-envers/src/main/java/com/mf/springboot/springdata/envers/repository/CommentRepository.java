package com.mf.springboot.springdata.envers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mf.springboot.springdata.envers.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer>{

}
