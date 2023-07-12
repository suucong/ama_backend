package com.example.ama_backend.service;

import com.example.ama_backend.config.JWTUtils;
import com.example.ama_backend.dto.IdTokenRequestDto;
import com.example.ama_backend.entity.*;
import com.example.ama_backend.persistence.QuestionRepository;
import com.example.ama_backend.persistence.SpaceRepository;
import com.example.ama_backend.persistence.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.ama_backend.service.QAService;
import com.example.ama_backend.entity.AnswerEntity;
import com.example.ama_backend.persistence.AnswerRepository;
import com.example.ama_backend.persistence.FollowRepository;
import com.example.ama_backend.entity.Follow;
import com.example.ama_backend.service.FollowService;
import com.example.ama_backend.persistence.SpaceRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final JWTUtils jwtUtils;
    private final GoogleIdTokenVerifier verifier;
    @Autowired
    private SpaceRepository spaceRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private QAService qaService;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private FollowService followService;
    private static final String CLIENT_ID = "666974459730-6bv37t0c044nns1tnhd8rrosnspbq613.apps.googleusercontent.com";


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
    public String loginOAuthGoogle(IdTokenRequestDto requestBody) throws GeneralSecurityException, IOException {
        System.out.println("loginOAuthGoogle");
        UserEntity userEntity=verifyCredential(requestBody.getIdToken());
        if(userEntity ==null){
            throw new IllegalArgumentException("null user");
        }
        userEntity=createOrUpdateUser(userEntity);
        // 스페이스 생성
        saveOrGet(userEntity.getId());
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
        // existingUser.setId(user.getId());                // 2023.06.06 로그인후 다시 로그인하면 id가 null로 넘어온 상태라 에러남... 확인해볼것~~~~
        existingUser.setIntroduce(user.getIntroduce());
        existingUser.setInstaId(user.getInstaId());
        existingUser.setProfileByte(user.getProfileByte());
        existingUser.setStopSpace(user.isStopSpace());

        return existingUser;
    }

    public UserEntity verifyCredential(String credential) throws GeneralSecurityException, IOException {
        NetHttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();

        System.out.println("verifycredential");
        GoogleIdToken idToken = GoogleIdToken.parse(jsonFactory, credential);
        if (idToken == null) {
            throw new IllegalArgumentException("Invalid credential");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String name = (String) payload.get("name");
        String email = payload.getEmail();
        String picture = (String) payload.get("picture");

        return new UserEntity(null, name, email, picture, null, null, null, Role.USER, null, false, false);
    }

    // 사진 압축
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

    public void updatePicture(UserEntity user, MultipartFile imgFile) throws Exception{
        byte[] imageBytes = imgFile.getBytes();
        byte[] compressedImageBytes;

        float compressionQuality = 0.1f; // 압축 품질 설정

        compressedImageBytes = compressImage(imageBytes, compressionQuality);
        user.setProfileByte(compressedImageBytes);
        System.out.println("Compressed Image Size: " + compressedImageBytes.length + " bytes");

        user.setProfileByte(compressedImageBytes);
        user.setPicture("https://api.mumul.space/picture/"+user.getId());

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

    public Boolean doSecession(Long userId) {
        Optional<UserEntity> userEntity = userRepository.findById(userId);

        if (userEntity.isPresent()) {
            UserEntity user = userEntity.get();

            List<QuestionEntity> sendingQuestionEntityList = questionRepository.findBySendingUserId(userId);
            List<QuestionEntity> receivingQuestionEntityList = questionRepository.findByReceivingUserId(userId);
            List<AnswerEntity> answerEntityList = answerRepository.findByUserId(userId);

            for (QuestionEntity q : sendingQuestionEntityList) {
                qaService.deleteQuestionAndAnswers(q.getId());
            }
            for (QuestionEntity q : receivingQuestionEntityList) {
                qaService.deleteQuestionAndAnswers(q.getId());
            }
            // answerId에 해당하는 답변을 가져옴
            for (AnswerEntity a : answerEntityList) {
                qaService.deleteAnswer(a.getId(), userId);
            }

            List<Follow> followingList = followService.getAllFollowings(user);
            List<Follow> followerList = followService.getAllFollowers(user);

            for (Follow f : followingList) {
                followService.deleteFollow(user, f.getToUser());
            }
            for (Follow f: followerList) {
                followService.deleteFollow(f.getFromUser(), user);
            }
            Optional<SpaceEntity> spaceEntity = spaceRepository.findById(userId);
            if (spaceEntity.isPresent()) {
                SpaceEntity space = spaceEntity.get();
                spaceRepository.delete(space);
            }
            userRepository.delete(user);
        }

        return true;
    }
}
