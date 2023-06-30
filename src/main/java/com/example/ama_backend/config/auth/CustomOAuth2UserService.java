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
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 소셜 로그인 이후 가져온 사용자의 정보(email,picture,nickname) 등을 기반으로 가입 및 정보 수정, 세션 저장 등의 기능을 지원함
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService {

    private final UserRepository userRepository;
    private final SpaceRepository spaceRepository;

    public static byte[] compressImage(byte[] imageBytes, float quality) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
        BufferedImage image = ImageIO.read(bais);

        // Create a blank, RGB, same width and height, and a white background
        BufferedImage compressedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        compressedImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);

        // Create a ByteArrayOutputStream to hold the compressed image data
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Write the compressed image to the ByteArrayOutputStream with the specified quality
        ImageIO.write(compressedImage, "jpg", baos);

        // Get the compressed image bytes from the ByteArrayOutputStream
        byte[] compressedBytes = baos.toByteArray();

        // Close the streams
        baos.close();
        bais.close();

        return compressedBytes;
    }

    // 사진 넣어주는 부분
    public void updatePicture(UserEntity user, MultipartFile imgFile) throws Exception{
        byte[] imageBytes = imgFile.getBytes();
        byte[] compressedImageBytes;

        float compressionQuality = 0.1f; // 압축 품질 설정

        compressedImageBytes = compressImage(imageBytes, compressionQuality);
        user.setProfileByte(compressedImageBytes);
        System.out.println("Compressed Image Size: " + compressedImageBytes.length + " bytes");

        user.setProfileByte(compressedImageBytes);
        user.setPicture("http://localhost:8080/spaces/"+user.getId()+"/picture");

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
