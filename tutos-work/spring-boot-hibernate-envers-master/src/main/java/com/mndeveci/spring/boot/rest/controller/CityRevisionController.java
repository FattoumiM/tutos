package com.mf.spring.boot.rest.controller;

import com.mf.spring.boot.rest.model.City;
import com.mf.spring.boot.rest.model.EntityWithRevision;
import com.mf.spring.boot.rest.repository.CityRevisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by mf on 18.11.2016.
 */

@RestController
@RequestMapping("/api/cities/history")
public class CityRevisionController {

    @Autowired
    private CityRevisionRepository cityRevisionRepository;


    @RequestMapping("/revisions/{cityCode}")
    public List<EntityWithRevision<City>> getCityRevisions(@PathVariable Integer cityCode) {
        return cityRevisionRepository.listCityRevisions(cityCode);
    }



}
