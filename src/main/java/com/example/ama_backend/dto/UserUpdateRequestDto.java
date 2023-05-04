package com.example.ama_backend.dto;

import com.example.ama_backend.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserUpdateRequestDto {
    private Long userId;
    private String name;
    private String picture;
    private String instaId;
    private String introduce;

    @Builder
    public UserUpdateRequestDto(Long id, String name, String picture, String instaId, String introduce) {
        this.userId=id;
        this.name = name;
        this.picture = picture;
        this.introduce = introduce;
        this.instaId = instaId;
    }

    public UserEntity toEntity(final UserUpdateRequestDto userUpdateRequestDto) {
        return UserEntity.builder()
                .id(userUpdateRequestDto.getUserId())
                .name(userUpdateRequestDto.getName())
                .picture(userUpdateRequestDto.getPicture())
                .instaId(userUpdateRequestDto.getInstaId())
                .introduce(userUpdateRequestDto.getIntroduce())
                .build();
    }

}
