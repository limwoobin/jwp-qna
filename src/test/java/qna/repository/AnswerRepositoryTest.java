package qna.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static qna.domain.AnswerTest.A1;
import static qna.domain.AnswerTest.A2;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import qna.domain.Answer;
import qna.domain.AnswerRepository;

@DataJpaTest
class AnswerRepositoryTest {

    @Autowired
    private AnswerRepository answerRepository;

    private Answer answer;
    private Answer answer2;

    @BeforeEach
    void setUp() {
        answer = A1;
        answer2 = A2;
        A1.setDeleted(false);
        A2.setDeleted(false);

        answer = answerRepository.save(answer);
        answer2 = answerRepository.save(answer2);
    }

    @DisplayName("엔티티를 저장하면 정상적으로 저장되어 id 값이 채번되어야 한다")
    @Test
    void save_test() {
        answerRepository.save(answer);
        assertThat(answer.getId()).isNotNull();
    }

    @DisplayName("같은 questionId 를 가진 엔티티를 questionId 로 조회하면 모두 조회되어야 한다")
    @Test
    void find_test() {
        List<Answer> answers = answerRepository.findByQuestionIdAndDeletedFalse(A1.getQuestionId());

        assertAll(
            () -> assertThat(answers.get(0).getId()).isEqualTo(answer.getId()),
            () -> assertThat(answers.get(1).getId()).isEqualTo(answer2.getId())
        );
    }

    @DisplayName("id 와 deleted 가 false 인 엔티티를 동일 조건으로 조회하면 모두 조회되어야 한다")
    @Test
    void find_test2() {
        answer2.setDeleted(true);
        answerRepository.save(answer2);

        Optional<Answer> result = answerRepository.findByIdAndDeletedFalse(answer.getId());
        Optional<Answer> result2 = answerRepository.findByIdAndDeletedFalse(answer2.getId());

        assertTrue(result.isPresent());
        assertFalse(result2.isPresent());
    }

    @DisplayName("엔티티의 deleted 를 변경하고 다시 조회하면 변경된 상태로 조회되어야 한다")
    @Test
    void update_test() {
        answer.setDeleted(true);
        answerRepository.save(answer);

        assertThat(answer.isDeleted()).isTrue();
    }

    @DisplayName("entity 를 삭제하면 정상적으로 삭제되어야 한다")
    @Test
    void delete_test() {
        List<Answer> answers = answerRepository.findAll();
        answerRepository.delete(answer);

        List<Answer> deleted_answers = answerRepository.findAll();

        assertAll(
            () -> assertThat(answers).hasSize(2),
            () -> assertThat(deleted_answers).hasSize(1)
        );
    }
}
