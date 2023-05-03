package com.example.ama_backend.dto;

import com.example.ama_backend.entity.Role;
import com.example.ama_backend.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String picture;
    private Role role;
    private String instaId;
    private String introduce;

    public UserDTO(UserEntity userEntity) {
        this.id= userEntity.getId();
        this.name= userEntity.getName();
        this.email=userEntity.getName();
        this.picture= userEntity.getPicture();
        this.role=userEntity.getRole();
        this.instaId=userEntity.getInstaId();
        this.introduce=userEntity.getIntroduce();
    }

    public static UserEntity toEntity(final UserEntity dto) {
        return UserEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .picture(dto.getPicture())
                .role(dto.getRole())
                .instaId(dto.getInstaId())
                .introduce(dto.getIntroduce())
                .build();
    }
}
