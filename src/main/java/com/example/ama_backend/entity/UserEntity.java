package com.example.ama_backend.entity;


import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"id", "email", "nickname", "role"})
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
public class UserEntity{
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id; //사용자에게 고유하게 부여되는 id

    //소셜 로그인 한 사용자를 식별할 수 있어야 하므로 Resource Server 에 넘겨주는 식별자 ID
//    @Column(unique = true, nullable = false)
//    private String oAuth2Id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String profileImageUrl;

    //private String introduction;

    @Column(nullable = false)
    // jpa로 데이터베이스로 저장할 때 Enum 값을 어떤 형태로 저장할지 결정
    // 기본적으로 int로 숫자가 저장됨
    // 숫자로 저장되면 데이터베이스로 확인될 때 그 값이 모든 코드를 의미하는지 알 수가 없다
    // 그래서 문자열로 지정될 수 있도록 선언함
    @Enumerated(value = EnumType.STRING)
    private Role role;

    @Builder
    public UserEntity(String nickname,String email,String profileImageUrl,Role role){
        this.nickname=nickname;
        this.email=email;
        this.profileImageUrl=profileImageUrl;
        this.role=role;
    }

    // 구글 등 리소스 서버의 사용자 정보가 업뎃됐을 때를 대비해 엔터티 클래스에도 update 함수 구현
    public UserEntity update(String nickname,String profileImageUrl){
        this.nickname=nickname;
        this.profileImageUrl=profileImageUrl;

        return this;
    }

    public String getRoleKey(){
        return this.role.getKey();
    }

}
