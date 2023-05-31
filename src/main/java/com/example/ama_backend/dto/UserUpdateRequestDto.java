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
    private String profileImgName;
    private String instaId;
    private String introduce;


    public UserEntity toEntity(final UserUpdateRequestDto userUpdateRequestDto) {
        return UserEntity.builder()
                .id(userUpdateRequestDto.getUserId())
                .name(userUpdateRequestDto.getName())
                .picture(userUpdateRequestDto.getPicture())
                .profileImgName(userUpdateRequestDto.getProfileImgName())
                .instaId(userUpdateRequestDto.getInstaId())
                .introduce(userUpdateRequestDto.getIntroduce())
                .build();
    }

    public static final UserUpdateRequestDto convertToDto(UserEntity userEntity) {
        return UserUpdateRequestDto.builder()
                .userId(userEntity.getId())
                .name(userEntity.getName())
                .picture(userEntity.getPicture())
                .instaId(userEntity.getInstaId())
                .introduce(userEntity.getIntroduce())
                .profileImgName(userEntity.getProfileImgName())
                .build();
    }

}
