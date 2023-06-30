package com.example.ama_backend.config.auth.dto;

import com.example.ama_backend.entity.Role;
import com.example.ama_backend.entity.UserEntity;
import lombok.*;

import java.io.Serializable;

/** SessionUser 에는 인증된 사용자 정보만 필요하다
 *
 * @Entity UserEntity 클래스를 SessionUser 로 사용하지 않는 이유:
 *  세션에 저장하기 위해 UserEntity 클래스를 세션에 저장하려고 하니 UserEntity 클래스에 직렬화를 구현하지 않았다는 에러가 난다
 *
 ** Entity 클래스는 직렬화 코드를 넣지 않는게 좋다
 ** 엔티티 클래스에는 언제 다른 엔터티와 관계가 형성될지 모른다
 ** @OneToMany, @ManyToOne 등 자식 엔터티를 갖고 있다면 직렬화 대상에 자식들까지 포함되니 성능이슈, 부수효과가 발생할 확률이 높다
 ** 그래서 직렬화 기능을 가진 DTO 를 하나 추가로 만든 것이 더 좋은 방법이다.
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SessionUser implements Serializable {
    private Long id;
    private String name;
    private String email;
    private String picture;
    private byte[] profileByte;
    private Role role;
    private String instaId;
    private String introduce;

    public SessionUser(UserEntity userEntity){
        this.id= userEntity.getId();
        this.name=userEntity.getName();
        this.email=userEntity.getEmail();
        this.role=userEntity.getRole();
        this.picture=userEntity.getPicture();
        this.profileByte=userEntity.getProfileByte();
        this.introduce=userEntity.getIntroduce();
        this.instaId=userEntity.getInstaId();
    }
}
