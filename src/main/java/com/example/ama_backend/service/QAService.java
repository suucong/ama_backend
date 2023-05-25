package com.example.ama_backend.service;

import com.example.ama_backend.entity.AnswerEntity;
import com.example.ama_backend.entity.QuestionEntity;
import com.example.ama_backend.persistence.AnswerRepository;
import com.example.ama_backend.persistence.QuestionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
// 메소드 단위로 트랜잭션을 적용하기 위한 것이다. 메소드 실행 중에 예외가 발생하면
// 해당 메소드에서 이루어진 모둔 데이터 변경 작업이롤백된다. 이를 통해 데이터 일관성을 유지할 수 있다.
@Transactional
public class QAService {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;


    //모든 질문과 답변을 조회하는 기능
    public List<QuestionEntity> getAllQuestions() {
        return questionRepository.findAll();
    }

    // 내가 보낸 질문 조회 기능
    public List<QuestionEntity> getMySendingQuestions(final Long sendingUserId) {
        return questionRepository.findBySendingUserId(sendingUserId);
    }

    // 내가 받은 질문 조회 기능
    public List<QuestionEntity> getMyReceivingQuestions(final Long receivingUserId) {
        return questionRepository.findByReceivingUserId(receivingUserId);
    }

    // 내가 한 답변을 조회하는 기능
    public Optional<AnswerEntity> getMyAnswers(final Long id) {
        return answerRepository.findById(id);
    }

    // 리팩토링한 질문 검증 메소드
    private void validateQuestion(final QuestionEntity questionEntity) {
        if (questionEntity == null) {
            log.warn("Question Entity 는 null 이면 안됩니다.");
            throw new RuntimeException("Question Entity 는 null 이면 안됩니다.");
        }
        //isAnonymous 값이 true 일 때 닉네임 "익명"
        if (questionEntity.getIsAnonymous()) {
            questionEntity.setUserId("😼익명의 냥이");
        }

        if (questionEntity.getUserId() == null) {
            log.warn("등록되지 않은 유저입니다.");
            throw new RuntimeException("등록되지 않은 유저입니다.");
        }

        if (questionEntity.getQuestionText() == null || questionEntity.getQuestionText().isEmpty()) {
            throw new IllegalArgumentException("질문 내용을 입력하세요.");
        }
    }

    // 리팩토링한 답변 검증 메소드
    private void validateAnswer(final AnswerEntity answerEntity) {
        if (answerEntity == null) {
            log.warn("Answer Entity 는 null 이면 안됩니다.");
            throw new RuntimeException("Answer Entity 는 null 이면 안됩니다.");
        }

        // 질문자나 답변자가 아니라면
        if (!answerEntity.getIsPublic()) {
            answerEntity.setAlternativeAnswerText("🔒질문자만 볼 수 있는 답변입니다.");
        }

        if (answerEntity.getUserId() == null) {
            log.warn("등록되지 않은 유저입니다.");
            throw new RuntimeException("등록되지 않은 유저입니다.");
        }


        if (answerEntity.getAnswerText() == null || answerEntity.getAnswerText().isEmpty()) {
            throw new IllegalArgumentException("답변 내용을 입력하세요.");
        }
    }

    // 질문 등록 가능 - 익명/닉네임 질문 둘 다 가능하도록
    public List<QuestionEntity> saveQuestion(final QuestionEntity questionEntity) {
        validateQuestion(questionEntity);
        questionRepository.save(questionEntity);
        log.info("엔터티 아이디 : {} 가 저장되었습니다.", questionEntity.getId());
        return questionRepository.findBySendingUserId(questionEntity.getSendingUserId());
    }


    // 답변 등록 기능 - 당연히 닉네임으로
    public Optional<AnswerEntity> saveAnswer(final AnswerEntity answerEntity) {
        validateAnswer(answerEntity);
        answerRepository.save(answerEntity);
        log.info("엔터티 아이디 : {} 가 저장되었습니다.", answerEntity.getId());
        return getMyAnswers(answerEntity.getId());
    }


    // 특정 질문과 그에 대한 모든 답변을 삭제하는 기능
    public List<QuestionEntity> deleteQuestionAndAnswers(Long questionId) {
        Optional<QuestionEntity> optionalQuestion = questionRepository.findById(questionId);

        if (optionalQuestion.isPresent()) {
            QuestionEntity question = optionalQuestion.get();
            List<AnswerEntity> answers = question.getAnswers();

            try {
                // 가져온 질문과 답변들을 삭제
                answerRepository.deleteAll(answers);
                questionRepository.delete(question);
            } catch (Exception e) {
                log.error("질문 엔터티 삭제 중 에러 발생", questionId, e);
                // 컨트롤러로 exception 을 보낸다. 데이터베이스 내부 로직을 캡슐화하려면 e를 리턴하지 않고 새 exception 오브젝트를 리턴한다
                throw new RuntimeException("질문 엔터티 삭제 중 에러 발생", e);
            }

            // 만약 내가 보낸 질문을 삭제하는 거면 내가 보낸 질문을 조회함
            if (questionId.equals(optionalQuestion.get().getSendingUserId())) {
                return getMySendingQuestions(questionId);
            }
            // 내가 받은 질문을 삭제하는 거면 내가 받은 질문을 조회함
            else if (questionId.equals(optionalQuestion.get().getReceivingUserId())) {
                return getMyReceivingQuestions(questionId);
            }
            // 그 외에는 모든 질문을 조회함
            else {
                return getAllQuestions();
            }
        } else {
            throw new IllegalArgumentException("질문을 찾을 수 없습니다.");
        }
    }

    // 답변을 삭제하는 기능
    public Optional<AnswerEntity> deleteAnswer(Long answerId,Long userId) {

        // answerId에 해당하는 답변을 가져옴
        Optional<AnswerEntity> optionalAnswer = answerRepository.findById(answerId);
        //존재하는 답변인지 조회하기
        if (optionalAnswer.isPresent()) {
            //내가 작성한 답변인지 조회하기
            if(optionalAnswer.get().isMyAnswer(userId)){
                AnswerEntity answer = optionalAnswer.get();
                try {
                    // 가져온 답변을 삭제
                    answerRepository.delete(answer);
                } catch (Exception e) {
                    log.error("답변 엔터티 삭제 중 에러 발생", answerId, e);
                    // 컨트롤러로 exception 을 보낸다. 데이터베이스 내부 로직을 캡슐화하려면 e를 리턴하지 않고 새 exception 오브젝트를 리턴한다
                    throw new RuntimeException("답변 엔터티 삭제 중 에러 발생", e);
                }
            }else{
                throw new IllegalArgumentException("내 답변이 아닙니다.");
            }


        } else {
            throw new IllegalArgumentException("답변을 찾을 수 없습니다.");
        }


        return getMyAnswers(answerId);
    }

}
