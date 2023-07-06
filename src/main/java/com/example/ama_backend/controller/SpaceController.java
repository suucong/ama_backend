package com.example.ama_backend.controller;


import com.example.ama_backend.config.auth.dto.SessionUser;
import com.example.ama_backend.dto.AnswerDTO;
import com.example.ama_backend.dto.QuestionDTO;
import com.example.ama_backend.dto.ResponseDTO;
import com.example.ama_backend.dto.UserUpdateRequestDto;
import com.example.ama_backend.entity.*;
import com.example.ama_backend.persistence.*;
import com.example.ama_backend.service.FollowService;
import com.example.ama_backend.service.QAService;
import com.example.ama_backend.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.ama_backend.config.auth.CustomOAuth2UserService;
import com.example.ama_backend.dto.FollowingDTO;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;


    @GetMapping("/{spaceId}")
    public ResponseEntity getSpaceInfo(@PathVariable Long spaceId) throws Exception {
        //이동한 스페이스 엔터티
        SpaceEntity space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));
        // 이동한 스페이스의 주인유저 엔터티
        UserEntity ownerUser = userRepository.findById(space.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));

        // 현재 로그인한 유저
        org.springframework.security.core.Authentication testAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if (testAuthentication != null) {
            // 현재 로그인한 유저 아이디
            long currentUserId = Long.parseLong((String) testAuthentication.getPrincipal());

            UserEntity user = userService.getUser(ownerUser.getId());
            System.out.println("owneruser.getid: " + ownerUser.getId());
            System.out.println("currentuserid:" + currentUserId);
            return ResponseEntity.ok().body(convertToDto(user));
        } else {
            return ResponseEntity.ok().body(convertToDto(ownerUser));
        }
    }

    @GetMapping(value = "/{spaceId}/picture", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE})
    public ResponseEntity<?> getProfileImg(@PathVariable Long spaceId) throws IOException {
        UserEntity user = userRepository.findById(spaceId).orElse(null);

        if (user != null && Objects.equals(user.getProfileByte(), "")) {
            return new ResponseEntity<>(user.getPicture(), HttpStatus.OK);
        } else {
            assert user != null;
            return new ResponseEntity<>(user.getProfileByte(), HttpStatus.OK);
        }
    }


    @PostMapping("/{spaceId}/follow")
    public ResponseEntity<String> follow(@PathVariable Long spaceId) {
        SpaceEntity space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));

        // 이동한 스페이스의 주인유저 엔터티(toUser)
        UserEntity ownerUser = userRepository.findById(space.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));

        // 현재 로그인한 유저;
        org.springframework.security.core.Authentication testAuthentication = SecurityContextHolder.getContext().getAuthentication();
        long currentUserId = Long.parseLong((String)testAuthentication.getPrincipal());

        UserEntity user = userService.getUser(currentUserId);
        System.out.println("owneruser.getid: "+ownerUser.getId());
        System.out.println("currentuserid:"+currentUserId);

        //팔로우하기
        assert user != null;
        followService.follow(user, ownerUser);

        return ResponseEntity.ok().body("ok");
    }

    @PostMapping("/{spaceId}/unFollow")
    public ResponseEntity<String> unFollow(@PathVariable Long spaceId) {
        try {
            //이동한 스페이스 엔터티
            SpaceEntity space = spaceRepository.findById(spaceId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid space id"));

            // 이동한 스페이스의 주인유저 엔터티
            UserEntity ownerUser = userRepository.findById(space.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));

            // 현재 로그인한 유저
            org.springframework.security.core.Authentication testAuthentication = SecurityContextHolder.getContext().getAuthentication();
            long currentUserId = Long.parseLong((String)testAuthentication.getPrincipal());

            UserEntity user = userService.getUser(currentUserId);
            System.out.println("owneruser.getid: "+ownerUser.getId());
            System.out.println("currentuserid:"+currentUserId);

            followService.deleteFollow(user, ownerUser);

            //세션에서 현재 유저정보 가져오기
            return ResponseEntity.ok().body("ok");
        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.badRequest().body("bad");
        }
    }

    @GetMapping("/isFollow/{spaceId}")
    public ResponseEntity<Boolean> isFollow(@PathVariable Long spaceId) {
        // 현재 로그인한 유저
        org.springframework.security.core.Authentication testAuthentication = SecurityContextHolder.getContext().getAuthentication();
        long currentUserId = Long.parseLong((String)testAuthentication.getPrincipal());

        UserEntity currentUser = userService.getUser(currentUserId);
        UserEntity spaceUser = userService.getUser(spaceId);

        boolean isFollowed = followRepository.findByFromUserAndToUser(currentUser, spaceUser).isPresent();

        return ResponseEntity.ok().body(isFollowed);
    }

    @GetMapping("/following/{spaceId}")
    public ResponseEntity<FollowingDTO<List<UserUpdateRequestDto>>> getFollowing(@PathVariable Long spaceId) {
        UserEntity spaceUser = userService.getUser(spaceId);

        List<Follow> followingList = followService.getAllFollowings(spaceUser);

        List<UserUpdateRequestDto> followingUserDtoList = new ArrayList<>();

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

    @GetMapping("/follower/{spaceId}")
    public ResponseEntity<FollowingDTO<List<UserUpdateRequestDto>>> getFollower(@PathVariable Long spaceId) {
        UserEntity spaceUser = userService.getUser(spaceId);

        List<Follow> followerList = followService.getAllFollowers(spaceUser);

        List<UserUpdateRequestDto> followerUserList = new ArrayList<>();

        for (Follow f : followerList) {
            UserEntity followerUser = f.getFromUser();
            System.out.println(followerUser.getId());
            followerUserList.add(convertToDto(followerUser));
        }

        FollowingDTO<List<UserUpdateRequestDto>> responseDTO = FollowingDTO.<List<UserUpdateRequestDto>>builder()
                .data(followerUserList)
                .build();

        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/followingNumber/{spaceId}")
    public ResponseEntity<Integer> getFollowingNumber(@PathVariable Long spaceId) {
        UserEntity spaceUser = userService.getUser(spaceId);

        List<Follow> followingList = followService.getAllFollowings(spaceUser);
        int followingCount = followingList.size();

        return ResponseEntity.ok().body(followingCount);
    }

    @GetMapping("/followerNumber/{spaceId}")
    public ResponseEntity<Integer> getFollowerNumber(@PathVariable Long spaceId) {
        UserEntity spaceUser = userService.getUser(spaceId);

        List<Follow> followingList = followService.getAllFollowers(spaceUser);
        int followerCount = followingList.size();

        return ResponseEntity.ok().body(followerCount);
    }

    @PutMapping("/user/update/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable Long userId, @RequestPart(value = "requestDto") UserUpdateRequestDto requestDto, @RequestPart(value = "imgFile", required = false) MultipartFile imgFile) throws Exception {
        UserEntity currentUser = userRepository.findById(userId).orElse(null);

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 서비스입니다.");
        }

        if (!currentUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("본인 계정만 수정할 수 있습니다.");
        }

        currentUser.setName(requestDto.getName());
        currentUser.setIntroduce(requestDto.getIntroduce());
        currentUser.setInstaId(requestDto.getInstaId());
        currentUser.setLink(requestDto.getLink());

        if (imgFile == null) {
            customOAuth2UserService.saveUserAccountWithoutProfile(currentUser);
        } else {
            customOAuth2UserService.updatePicture(currentUser, imgFile);
        }

        return ResponseEntity.ok("수정이 완료되었습니다.");
    }

}
