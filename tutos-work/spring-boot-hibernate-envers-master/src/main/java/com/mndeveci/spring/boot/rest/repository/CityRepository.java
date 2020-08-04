package com.mf.spring.boot.rest.repository;

import com.mf.spring.boot.rest.model.City;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CityRepository extends CrudRepository<City, Integer> {

    List<City> findByName(String name);

}
