package lab3.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Arrays;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "text_lessons", schema = "public")
@PrimaryKeyJoinColumn(name = "lesson_id")
@Data
@NoArgsConstructor
public class TextLesson extends Lesson {

    @Column(name = "text_content", columnDefinition = "text")
    private String textContent;

    @Column(name = "translation", columnDefinition = "text")
    private String translation;

    @Column(name = "complex_words", columnDefinition = "text")
    private String complexWords;

    @Column(name = "annotation", columnDefinition = "text")
    private String annotation;

    @Override
    public String getType() {
        return "Текст";
    }

    @Override
    public String getMaterials() {
        return textContent != null ? textContent : "";
    }

    @Override
    public double getDuration() {
        int words = getWordCount();
        if (words == 0) return 0.0;
        return ((double) words / 200.0) * 60.0;
    }

    @Transient
    public int getWordCount() {
        if (textContent == null || textContent.isBlank()) return 0;
        return textContent.trim().split("\\s+").length;
    }

    @Transient
    public String[] getComplexWordsAsArray() {
        if (complexWords == null || complexWords.isBlank()) return new String[0];
        return Arrays.stream(complexWords.split(";"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
    }

    public void showTextDetails() {
        System.out.println("\nТекст: " + getMaterials());
        System.out.println("\nПеревод: " + (translation == null ? "-" : translation));
        System.out.println("\nАннотация: " + (annotation == null ? "-" : annotation));
        System.out.println("\nСложные слова: " + String.join(", ", getComplexWordsAsArray()));
    }

    public BigDecimal calculateSpeed(Duration actualDuration) {
        double seconds = actualDuration.toMillis() / 1000.0;
        double minutes = seconds / 60.0;
        if (minutes <= 0) {
            return BigDecimal.ZERO.setScale(2);
        }
        return BigDecimal.valueOf(getWordCount() / minutes)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
