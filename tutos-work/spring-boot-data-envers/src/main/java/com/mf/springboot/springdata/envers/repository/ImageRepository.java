package com.mf.springboot.springdata.envers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mf.springboot.springdata.envers.entity.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer>{

}
