package com.example.ama_backend.service;

import com.example.ama_backend.config.JWTUtils;
import com.example.ama_backend.entity.*;
import com.example.ama_backend.persistence.QuestionRepository;
import com.example.ama_backend.persistence.SpaceRepository;
import com.example.ama_backend.persistence.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
//import com.google.api.client.http.HttpHeaders;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.example.ama_backend.entity.AnswerEntity;
import com.example.ama_backend.persistence.AnswerRepository;
import com.example.ama_backend.persistence.FollowRepository;
import com.example.ama_backend.entity.Follow;
import org.springframework.http.HttpHeaders;
import javax.imageio.ImageIO;
import java.awt.*;
import java.util.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;


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
    @Value("${oauth2.google.token-url}")
    private String GOOGLE_TOKEN_URL;
    @Value("${oauth2.google.client-id}")
    private String GOOGLE_CLIENT_ID;
    @Value("${oauth2.google.client-secret}")
    private String GOOGLE_CLIENT_SECRET;
    @Value("${oauth2.google.redirect-uri}")
    private String LOGIN_REDIRECT_URL;
    @Value("${oauth2.kakao.client-id}")
    private String clientId;
    @Value("${oauth2.kakao.redirect-uri}")
    private String KAKAO_REDIRECT_URL;

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

    public String getGoogleAccessToken(String accessCode) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> params = new HashMap<>();

        params.put("code", accessCode);
        params.put("client_id", GOOGLE_CLIENT_ID);
        params.put("client_secret", GOOGLE_CLIENT_SECRET);
        params.put("redirect_uri", LOGIN_REDIRECT_URL);
        params.put("grant_type", "authorization_code");

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(GOOGLE_TOKEN_URL, params, String.class);

        if(responseEntity.getStatusCode() == HttpStatus.OK) {
            String responseBody = responseEntity.getBody();

            // JSON 파싱을 위해 ObjectMapper 사용
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                // JSON 문자열에서 "id_token" 필드의 값 추출
                Map<String, Object> responseMap = objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});

                return (String) responseMap.get("id_token");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public String getAccessToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", KAKAO_REDIRECT_URL);
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        return jsonNode.get("access_token").asText();
    }

    public String loginOauthKakao(String accessToken) throws GeneralSecurityException, IOException {
        System.out.println("======loginOauthKakao======");
        UserEntity userEntity = getKakaoUserInfo(accessToken);
        userEntity = createOrUpdateUser(userEntity);
        // 스페이스 생성
        saveOrGet(userEntity.getId());

        return jwtUtils.createToken(userEntity, false);
    }

    // Google OAuth 로부터 받은 ID 토큰을 검증하여 UserEntity 를 인증하고 JWT 토큰을 생성하여 반환한다
    public String loginOAuthGoogle(String idToken) throws GeneralSecurityException, IOException {
        System.out.println("======loginOAuthGoogle=======");
        UserEntity userEntity=verifyCredential(idToken);
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
        UserEntity existingUser = userRepository.findByIsKakaoUserAndEmail(user.isKakaoUser(), user.getEmail()).orElse(null);
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
        existingUser.setKakaoUser(user.isKakaoUser());

        return existingUser;
    }

    public UserEntity getKakaoUserInfo(String accessToken) throws JsonProcessingException, IOException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readTree(responseBody);

        String nickname = jsonNode.get("properties").get("nickname").asText();
        Boolean hasEmail = jsonNode.get("kakao_account").get("has_email").asBoolean();
        String email = "";
        if(hasEmail) {
            email = jsonNode.get("kakao_account").get("email").asText();
        }
        String picture = jsonNode.get("kakao_account").get("profile").get("profile_image_url").asText();

        return new UserEntity(null, nickname, email, picture, null, null, null, Role.USER, null, false, false, true);
    }

    public UserEntity verifyCredential(String credential) throws GeneralSecurityException, IOException {
        NetHttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();

        GoogleIdToken idToken = GoogleIdToken.parse(jsonFactory, credential);
        if (idToken == null) {
            throw new IllegalArgumentException("Invalid credential");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String name = (String) payload.get("name");
        String email = payload.getEmail();
        String picture = (String) payload.get("picture");

        return new UserEntity(null, name, email, picture, null, null, null, Role.USER, null, false, false, false);
    }

    // 사진 압축
    public static byte[] compressImage(byte[] imageBytes, float quality) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
        BufferedImage image = ImageIO.read(bais);

        // 이미지를 압축하기 위해 빈 BufferedImage를 생성한다. RGB 형식이며 원본 이미지와 같은 너비와 높이를 가지고 있으며 흰 배경을 가지고 있다.
        BufferedImage compressedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        compressedImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);

        // 압축된 이미지 데이터를 보관할 ByteArrayOutputStream을 생성
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // 지정된 품질로 압축된 이미지를 ByteArrayOutputStream에 작성
        ImageIO.write(compressedImage, "jpg", baos);

        // ByteArrayOutputStream에서 압축된 이미지 바이트를 얻는다
        byte[] compressedBytes = baos.toByteArray();

        // 스트림을 닫는다
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

    // 회원 탈퇴 메소드
    public void doSecession(Long userId) {
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
    }
}
