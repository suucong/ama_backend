package com.example.ama_backend.config.auth;

import com.example.ama_backend.config.auth.dto.OAuthAttributes;
import com.example.ama_backend.config.auth.dto.SessionUser;
import com.example.ama_backend.dto.UserUpdateRequestDto;
import com.example.ama_backend.entity.SpaceEntity;
import com.example.ama_backend.entity.UserEntity;
import com.example.ama_backend.persistence.SpaceRepository;
import com.example.ama_backend.persistence.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

/**
 * 소셜 로그인 이후 가져온 사용자의 정보(email,picture,nickname) 등을 기반으로 가입 및 정보 수정, 세션 저장 등의 기능을 지원함
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService {

    private final UserRepository userRepository;
    private final SpaceRepository spaceRepository;

    // 사진 넣어주는 부분
    public void updatePicture(UserEntity user, MultipartFile imgFile) throws Exception{

        String projectPath = System.getProperty("user.dir") + "/src/main/resources/files";

        UUID uuid = UUID.randomUUID();

        String fileName = uuid + "_" + imgFile.getOriginalFilename();

        File saveFile = new File(projectPath, fileName);

        imgFile.transferTo(saveFile);

        user.setProfileImgName(fileName);
        user.setPicture(projectPath + "/" + fileName);

        userRepository.save(user);
    }

    public void saveUserAccountWithoutProfile(UserEntity user) {
        userRepository.save(user);
    }

    public SpaceEntity saveOrGet(Long userId) {
        Optional<SpaceEntity> optionalSpaceEntity = spaceRepository.findByUserId(userId);
        if (optionalSpaceEntity.isPresent()) {
            // 이전에 생성한 스페이스가 있다면 그 스페이스를 반환
            return optionalSpaceEntity.get();
        } else if (userId != null) { // userId가 null이 아닌 경우에만 새로운 SpaceEntity를 생성합니다.
            // 이전에 생성한 스페이스가 없다면 새로 생성하여 반환
            SpaceEntity space = new SpaceEntity();
            space.setUserId(userId);
            return spaceRepository.save(space);
        } else {
            return null;
        }
    }
}
