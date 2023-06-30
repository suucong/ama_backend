package com.example.ama_backend.dto;

import com.example.ama_backend.entity.UserEntity;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestDto {
    private Long userId;
    private String name;
    private String picture;
    private byte[] profileByte;
    private String instaId;
    private String introduce;
    private String link;


    public UserEntity toEntity(final UserUpdateRequestDto userUpdateRequestDto) {
        return UserEntity.builder()
                .id(userUpdateRequestDto.getUserId())
                .name(userUpdateRequestDto.getName())
                .picture(userUpdateRequestDto.getPicture())
                .profileByte(userUpdateRequestDto.getProfileByte())
                .instaId(userUpdateRequestDto.getInstaId())
                .introduce(userUpdateRequestDto.getIntroduce())
                .link(userUpdateRequestDto.getLink())
                .build();
    }

    public static final UserUpdateRequestDto convertToDto(UserEntity userEntity) {
        return UserUpdateRequestDto.builder()
                .userId(userEntity.getId())
                .name(userEntity.getName())
                .picture(userEntity.getPicture())
                .instaId(userEntity.getInstaId())
                .introduce(userEntity.getIntroduce())
                .profileByte(userEntity.getProfileByte())
                .link(userEntity.getLink())
                .build();
    }

}
