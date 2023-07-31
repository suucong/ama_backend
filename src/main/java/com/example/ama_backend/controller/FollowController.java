package com.example.ama_backend.controller;

import com.example.ama_backend.dto.FollowingDTO;
import com.example.ama_backend.dto.UserUpdateRequestDto;
import com.example.ama_backend.entity.Follow;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.ama_backend.entity.SpaceEntity;
import com.example.ama_backend.entity.UserEntity;
import org.springframework.http.ResponseEntity;

import com.example.ama_backend.persistence.SpaceRepository;
import com.example.ama_backend.persistence.UserRepository;
import com.example.ama_backend.persistence.FollowRepository;

import com.example.ama_backend.service.UserService;
import com.example.ama_backend.service.FollowService;

import java.util.ArrayList;
import java.util.List;

import static com.example.ama_backend.dto.UserUpdateRequestDto.convertToDto;

@Controller
public class FollowController {
    @Autowired
    private SpaceRepository spaceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private FollowService followService;

    // 현재 로그인한 유저가 스페이스 유저 팔로우
    @PostMapping("/follow/{spaceId}")
    public ResponseEntity<?> follow(@PathVariable Long spaceId) {
        System.out.println("~~~~~~follow~~~~~~~");
        org.springframework.security.core.Authentication Authentication = SecurityContextHolder.getContext().getAuthentication();
        long currentUserId = Long.parseLong((String)Authentication.getPrincipal());

        try {
            SpaceEntity space = spaceRepository.findById(spaceId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));

            // 이동한 스페이스의 주인유저 엔터티(toUser)
            UserEntity ownerUser = userRepository.findById(space.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));

            // 현재 로그인한 유저
            UserEntity user = userService.getUser(currentUserId);

            //팔로우하기
            assert user != null;
            followService.follow(user, ownerUser);

            return ResponseEntity.ok().body(true);
        } catch(Exception e) {
            e.printStackTrace();

            return ResponseEntity.badRequest().body(false);
        }
    }
    // 현재 로그인한 유저가 스페이스 유저 언팔로우
    @PostMapping("/unFollow/{spaceId}")
    public ResponseEntity<?> unFollow(@PathVariable Long spaceId) {
        System.out.println("~~~~~~unFollow~~~~~~");
        org.springframework.security.core.Authentication testAuthentication = SecurityContextHolder.getContext().getAuthentication();
        long currentUserId = Long.parseLong((String)testAuthentication.getPrincipal());

        try {
            //이동한 스페이스 엔터티
            SpaceEntity space = spaceRepository.findById(spaceId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));

            // 이동한 스페이스의 주인유저 엔터티
            UserEntity ownerUser = userRepository.findById(space.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));

            UserEntity user = userService.getUser(currentUserId);

            // 팔로우 정보 삭제
            followService.deleteFollow(user, ownerUser);

            return ResponseEntity.ok().body(true);
        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.badRequest().body(false);
        }
    }

    // 현재 로그인한 유저가 스페이스 주인유저를 팔로우했는지 아닌지 확인
    @GetMapping("/isFollow/{spaceId}")
    public ResponseEntity<?> isFollow(@PathVariable Long spaceId) {
        // 현재 로그인한 유저
        org.springframework.security.core.Authentication testAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if (testAuthentication == null) {
            return ResponseEntity.ok().body(false);
        } else {
            long currentUserId = Long.parseLong((String)testAuthentication.getPrincipal());
            UserEntity currentUser = userService.getUser(currentUserId);

            // 스페이스 주인 유저 정보 가져오기
            UserEntity spaceUser = userService.getUser(spaceId);

            // 현재 로그인한 유저가 스페이스 주인 유저를 팔로우했는지 확인
            boolean isFollowed = followRepository.findByFromUserAndToUser(currentUser, spaceUser).isPresent();

            return ResponseEntity.ok().body(isFollowed);
        }
    }

    @GetMapping("/isFollower/{spaceId}")
    public ResponseEntity<?> isFollower(@PathVariable Long spaceId) {
        // 현재 로그인한 유저
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return ResponseEntity.ok().body(false);
        } else {
            long currentUserId = Long.parseLong((String)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            UserEntity currentUser = userService.getUser(currentUserId);

            // 스페이스 주인 유저 정보 가져오기
            UserEntity spaceUser = userService.getUser(spaceId);

            // 현재 로그인한 유저가 스페이스 주인 유저를 팔로우했는지 확인
            boolean isFollowed = followRepository.findByFromUserAndToUser(spaceUser, currentUser).isPresent();

            return ResponseEntity.ok().body(isFollowed);
        }
    }

    // 스페이스 주인의 팔로잉 리스트 전달하기
    @GetMapping("/getFollow/following/{spaceId}")
    public ResponseEntity<FollowingDTO<List<UserUpdateRequestDto>>> getFollowing(@PathVariable Long spaceId) {
        // 스페이스 주인 유저 정보 가져오기
        UserEntity spaceUser = userService.getUser(spaceId);
        // 스페이스 주인이 팔로우한 유저 엔타티 정보 리스트로 모두 가져오기
        List<Follow> followingList = followService.getAllFollowings(spaceUser);

        // 프론트로 반환할 Dto 리스트 생성
        List<UserUpdateRequestDto> followingUserDtoList = new ArrayList<>();

        // 리스트에 팔로잉한 유저정보 넣어주기
        for (Follow f : followingList) {
            UserEntity followingUser = f.getToUser();
            System.out.println(followingUser.getId());
            followingUserDtoList.add(convertToDto(followingUser));
        }

        FollowingDTO<List<UserUpdateRequestDto>> responseDTO = FollowingDTO.<List<UserUpdateRequestDto>>builder()
                .data(followingUserDtoList)
                .build();

        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/getFollow/follower/{spaceId}")
    public ResponseEntity<FollowingDTO<List<UserUpdateRequestDto>>> getFollowers(@PathVariable Long spaceId) {
        // 스페이스 주인 유저 정보 가져오기
        UserEntity spaceUser = userService.getUser(spaceId);
        // 스페이스 주인이 팔로우한 유저 엔타티 정보 리스트로 모두 가져오기
        List<Follow> followerList = followService.getAllFollowers(spaceUser);

        // 프론트로 반환할 Dto 리스트 생성
        List<UserUpdateRequestDto> followerUserDtoList = new ArrayList<>();

        // 리스트에 팔로잉한 유저정보 넣어주기
        for (Follow f : followerList) {
            UserEntity followerUser = f.getFromUser();
            System.out.println(followerUser.getId());
            followerUserDtoList.add(convertToDto(followerUser));
        }

        FollowingDTO<List<UserUpdateRequestDto>> responseDTO = FollowingDTO.<List<UserUpdateRequestDto>>builder()
                .data(followerUserDtoList)
                .build();

        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/getFollow/followingNumber/{spaceId}")
    public ResponseEntity<Integer> getFollowingNumber(@PathVariable Long spaceId) {
        UserEntity spaceUser = userService.getUser(spaceId);

        List<Follow> followingList = followService.getAllFollowings(spaceUser);
        int followingCount = followingList.size();

        return ResponseEntity.ok().body(followingCount);
    }

    @GetMapping("/getFollow/followerNumber/{spaceId}")
    public ResponseEntity<Integer> getFollowerNumber(@PathVariable Long spaceId) {
        UserEntity spaceUser = userService.getUser(spaceId);

        List<Follow> followingList = followService.getAllFollowers(spaceUser);
        int followerCount = followingList.size();

        return ResponseEntity.ok().body(followerCount);
    }
}
