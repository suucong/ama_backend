package com.example.ama_backend.controller;

import com.example.ama_backend.entity.*;
import com.example.ama_backend.persistence.*;
import com.example.ama_backend.service.FollowService;
import com.example.ama_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

import static com.example.ama_backend.dto.UserUpdateRequestDto.convertToDto;

@Controller
@RequestMapping("/spaces")
public class SpaceController {
    @Autowired
    private SpaceRepository spaceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private FollowService followService;

    @GetMapping("/{spaceId}")
    public ResponseEntity getSpaceInfo(@PathVariable String spaceId) throws Exception {
        try {
            long id = Long.parseLong(spaceId); // 문자열을 long으로 변환
            Optional<SpaceEntity> spaceEntity = spaceRepository.findById(id);

            if(spaceEntity.isPresent()) {
                SpaceEntity space = spaceEntity.get();
                Optional<UserEntity> user = userRepository.findById(space.getUserId());
                if(user.isPresent()) {
                    UserEntity spaceUser = user.get();
                    return ResponseEntity.ok().body(convertToDto(spaceUser));
                } else {
                    return ResponseEntity.ok().body(false);
                }
            } else {
                return ResponseEntity.ok().body(false);
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.ok().body(false);
        }
    }
}
