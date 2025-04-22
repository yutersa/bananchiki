package lab3.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "lesson_results", schema = "public")
@Data
@NoArgsConstructor
@ToString(exclude = "lesson")
public class LessonResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "duration", precision = 10, scale = 2)
    private BigDecimal duration;

    @Column(name = "result", precision = 5, scale = 2)
    private BigDecimal result;

    @ManyToOne(fetch = FetchType.EAGER)

    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;
    @Override
    public String toString() {
        String type = lesson.getType();
        String resultString = type.equals("Тест")
                ? result.setScale(2) + "%"
                : result.setScale(2) + " слов/мин";
        return String.format("Урок: %-30s | Длительность: %8.2f сек | Результат: %s",
                lesson.getTitle(),
                duration,
                resultString);
    }

}