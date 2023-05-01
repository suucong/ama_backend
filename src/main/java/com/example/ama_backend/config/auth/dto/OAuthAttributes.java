package com.example.ama_backend.config.auth.dto;

import com.example.ama_backend.entity.Role;
import com.example.ama_backend.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/** OAuth2UserService 를 통해 가져온 OAuth2User 의 attribute 를 담을 클래스 */
@Getter
public class OAuthAttributes {
    private Map<String,Object> attributes;
    private String nameAttributeKey;
    private String nickname;
    private String email;
    private String profileImageUrl;

    @Builder
    public OAuthAttributes(Map<String,Object> attributes, String nameAttributeKey, String nickname,
                           String email, String profileImageUrl){
        this.attributes=attributes;
        this.nameAttributeKey=nameAttributeKey;
        this.nickname=nickname;
        this.email=email;
        this.profileImageUrl=profileImageUrl;
    }

    // OAuth2User 에서 반환하는 사용자 정보는 Map 이기 때문에 값 하나하나를 변환해야만 함
    public static OAuthAttributes of(String registrationId, String userNameAttributeName,
                                     Map<String,Object> attributes){
        return ofGoogle(userNameAttributeName,attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String,Object> attributes){
        return OAuthAttributes.builder()
                .nickname((String) attributes.get("nickname"))
                .email((String) attributes.get("email"))
                .profileImageUrl((String) attributes.get("profileImgUrl"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    // UserEntity 엔터티를 생성한다
    // OAuthAttributes 에서 엔터티를 생성하는 시점은 처음 가입할 때이다.
    // 가입할 때의 기본 권할을 GUEST 로 주기 위해서 ROLE 빌더 값에 Role.GUEST 를 사용함
    public UserEntity toEntity(){
        return UserEntity.builder()
                .nickname(nickname)
                .email(email)
                .profileImageUrl(profileImageUrl)
                .role(Role.GUEST)
                .build();
    }

}
