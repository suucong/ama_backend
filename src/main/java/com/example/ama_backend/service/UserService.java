package com.example.ama_backend.service;

import com.example.ama_backend.config.JWTUtils;
import com.example.ama_backend.dto.IdTokenRequestDto;
import com.example.ama_backend.entity.Role;
import com.example.ama_backend.entity.UserEntity;
import com.example.ama_backend.persistence.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JWTUtils jwtUtils;
    private final GoogleIdTokenVerifier verifier;


    // UserService 클래스의 생성자이다. 필요한 의존성을 주입받는다.
    public UserService(@Value("${app.googleClientId}") String clientId, UserRepository userRepository, JWTUtils jwtUtils) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        NetHttpTransport transport=new NetHttpTransport();
        JsonFactory jsonFactory= new JacksonFactory();

        // GoogleIdTokenVerifier 를 생성한다. Google API를 사용하여 ID 토큰을 검증할 수 있다.
        this.verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    // 지정된 ID로 유저를 조회한다. ID에 해당하는 유저가 없을경우 NULL 반환한다
    public UserEntity getUser(Long id){
        return userRepository.findById(id).orElse(null);
    }

    // Google OAuth 로부터 받은 ID 토큰을 검증하여 UserEntity 를 인증하고 JWT 토큰을 생성하여 반환한다
    public String loginOAuthGoogle(IdTokenRequestDto requestBody){
        UserEntity userEntity=verifyIDToken(requestBody.getIdToken());
        if(userEntity ==null){
            throw new IllegalArgumentException("유저가 NULL 입니다.");
        }
        userEntity=createOrUpdateUser(userEntity);
        return jwtUtils.createToken(userEntity, false);

    }

    // 주어진 유저를 생성하거나 업데이트한다. 이미 존재하는 유저라면 업데이트하고, 존재하지 않는 경우 새로운 유저로 생성한다
    @Transactional
    public UserEntity createOrUpdateUser(UserEntity user){
        UserEntity existingUser = userRepository.findByEmail(user.getEmail()).orElse(null);
        // 존재하지 않는 경우
        if(existingUser==null){
            user.setRole(Role.USER);
            userRepository.save(user);
            return user;
        }
        // 존재하는 경우
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPicture(user.getPicture());
        existingUser.setRole(user.getRole());
        existingUser.setId(user.getId());
        existingUser.setIntroduce(user.getIntroduce());
        existingUser.setInstaId(user.getInstaId());
        existingUser.setProfileImgName(user.getProfileImgName());

        return existingUser;
    }

    // 주어진 ID 토큰을 검증하고 토큰에 포함된 정보를 사용하여 유저 객체를 생성한다
    private UserEntity verifyIDToken(String idToken) {
        try{
            GoogleIdToken idTokenObj=verifier.verify(idToken);
            if(idTokenObj==null){
                return null;
            }

            GoogleIdToken.Payload payload=idTokenObj.getPayload();
            String name=(String) payload.get("name");
            String email= payload.getEmail();
            String picture=(String) payload.get("picture");
            return new UserEntity(null, name, email, picture, null, null, null, Role.USER);
        }catch(GeneralSecurityException | IOException e){
            return null;
        }
    }
}
