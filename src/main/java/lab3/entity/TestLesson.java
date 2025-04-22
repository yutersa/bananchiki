package lab3.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "test_lessons", schema = "public")
@PrimaryKeyJoinColumn(name = "lesson_id")
@Data
@NoArgsConstructor
public class TestLesson extends Lesson {

    @Column(name = "questions", columnDefinition = "text")
    private String questions;

    @Column(name = "answers", columnDefinition = "text")
    private String answers;

    @Transient
    private List<String> userAnswers;

    @Override
    public String getType() {
        return "Тест";
    }

    @Override
    public String getMaterials() {
        String[] qs = getQuestionsAsArray();
        String[] ans = getAnswersAsArray();
        if (qs.length == 0) {
            return "В этом тесте нет вопросов.";
        }
        StringBuilder sb = new StringBuilder();
        int count = Math.min(qs.length, ans.length);
        for (int i = 0; i < count; i++) {
            sb.append(String.format("Вопрос %d: %s%n", i+1, qs[i]));
            sb.append(String.format("Ответ: %s%n%n", ans[i]));
        }
        for (int i = count; i < qs.length; i++) {
            sb.append(String.format("Вопрос %d: %s%n", i+1, qs[i]));
            sb.append("Ответ: (нет данных)\n\n");
        }
        return sb.toString();
    }

    @Override
    public double getDuration() {
        String[] correct = getAnswersAsArray();
        if (userAnswers == null || correct.length == 0) {
            return 0.0;
        }
        int ok = 0;
        int cnt = Math.min(userAnswers.size(), correct.length);
        for (int i = 0; i < cnt; i++) {
            if (userAnswers.get(i) != null
                    && userAnswers.get(i).trim().equalsIgnoreCase(correct[i].trim())) {
                ok++;
            }
        }
        return Math.round(100.0 * ok / correct.length * 100) / 100.0;
    }

    @Transient
    private String[] getQuestionsAsArray() {
        if (questions == null || questions.isBlank()) return new String[0];
        return Arrays.stream(questions.split(";"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
    }

    @Transient
    private String[] getAnswersAsArray() {
        if (answers == null || answers.isBlank()) return new String[0];
        return Arrays.stream(answers.split(";"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
    }

    public void showQuestions() {
        String[] qs = getQuestionsAsArray();
        for (int i = 0; i < qs.length; i++) {
            System.out.printf("Вопрос %d: %s%n", i+1, qs[i]);
        }
    }

    public double grade(List<String> answersFromUser) {
        this.userAnswers = answersFromUser;
        return getDuration();
    }
    @Override
    public String toString() {
        return super.toString();
    }

}
