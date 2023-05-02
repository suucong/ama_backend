package com.example.ama_backend.service;

import com.example.ama_backend.entity.SpaceEntity;
import com.example.ama_backend.persistence.SpaceRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SpaceService {

    @Autowired
    private final SpaceRepository spaceRepository;

    @Autowired
    public SpaceService(SpaceRepository spaceRepository) {
        this.spaceRepository = spaceRepository;
    }


    public Optional<SpaceEntity> getSpaceById(Long id) {
        return spaceRepository.findById(id);
    }


    public SpaceEntity createSpace(SpaceEntity space) {
        return spaceRepository.save(space);
    }

}
