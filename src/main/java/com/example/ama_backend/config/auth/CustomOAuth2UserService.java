package com.example.ama_backend.config.auth;

import com.example.ama_backend.config.auth.dto.OAuthAttributes;
import com.example.ama_backend.config.auth.dto.SessionUser;
import com.example.ama_backend.entity.SpaceEntity;
import com.example.ama_backend.entity.UserEntity;
import com.example.ama_backend.persistence.SpaceRepository;
import com.example.ama_backend.persistence.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

/**
 * 소셜 로그인 이후 가져온 사용자의 정보(email,picture,nickname) 등을 기반으로 가입 및 정보 수정, 세션 저장 등의 기능을 지원함
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final HttpSession httpSession;
    private final SpaceRepository spaceRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        // 현재 로그인 진행 중인 서비스를 구분하는 코드
        // 카카오 로그인인지, 구글 로그인인지 등 구분하기 위해 사용함
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // OAuth2 로그인 진행 시 키가 되는 필드값임. Prime key와 같은 의미다
        // 구글은 기본적으로 코드를 지원하지만, 카카오는 기본 지원하지 않는다. 구글의 기본코드는 "sub"이다
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // OAuth2Service를 통해 가져온 OAuth2User의 attribute를 담을 클래스이다
        // 이후 카카오나 네이버 같은 소셜 로그인이 이 클래스 사용함
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        // 유저엔터티 관련 메소드
        UserEntity userEntity = loadOrSave(attributes);
        // SessionUser : 세션에 사용자 정보를 저장하기 위핸 DTO 클래스다

        httpSession.setAttribute("user", new SessionUser(userEntity));

        // 스페이스 생성 관련 메소드
       saveOrGet(userEntity.getId());

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(userEntity.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey()
        );

    }


    protected UserEntity loadOrSave(OAuthAttributes attributes) {
        // 로그인하려는 이메일이 유저엔터티에 이미 존재하는 이메일인지 여부 확인
        UserEntity userEntity = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> {
                    // 존재한다면 유저 엔터티를 반환한다
                    entity.update(attributes.getId(), attributes.getName(), attributes.getPicture(), attributes.getIntroduce(), attributes.getInstaId());
                    return entity;
                })
                // 존재하지 않는다면 엔터티를 새로 생성한다
                .orElseGet(() -> userRepository.save(attributes.toEntity()));

        return userEntity;
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
