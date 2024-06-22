package com.example.datacollectiondispatcher.repository;

import com.example.datacollectiondispatcher.entity.ServerEntities;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StationsRepository extends CrudRepository<ServerEntities,Integer> {
    List<ServerEntities> findAll();
}
