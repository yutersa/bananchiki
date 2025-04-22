package lab3.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "lessons", schema = "public")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
public abstract class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "difficulty", length = 50)
    private String difficulty;

    @Column(name = "language", length = 50)
    private String language;

    @OneToMany(mappedBy = "lesson",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<LessonResult> lessonResults = new ArrayList<>();

    @Transient
    private LocalDateTime startTime;

    public abstract String getType();

    public abstract String getMaterials();
    public abstract double getDuration();

    public void start() {
        startTime = LocalDateTime.now();
        System.out.printf("Урок \"%s\" (%s) начат",
                title, getType(), startTime);
    }

    public double end() {

        LocalDateTime endTime = LocalDateTime.now();
        Duration actual = Duration.between(startTime, endTime);
        double actualSec = actual.toMillis() / 1000.0;
        double recordSec = Math.max(actualSec, getDuration());
        System.out.printf("Урок \"%s\" завершён. Затраченное время: %.2f сек",
                title, actualSec, recordSec);
        return recordSec;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (Сложность: %s, Язык: %s)",
                getType(),
                title,
                difficulty,
                language);
    }
}
