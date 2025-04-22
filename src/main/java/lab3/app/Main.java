package lab3.app;

import lab3.entity.Lesson;
import lab3.entity.TextLesson;
import lab3.entity.TestLesson;
import lab3.entity.LessonResult;
import lab3.repository.LessonRepository;
import lab3.util.HibernateUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final LessonRepository repo = new LessonRepository();

    public static void main(String[] args) {
        try {
            runApp();
        } finally {
            HibernateUtil.shutdown();
            scanner.close();
            System.out.println("Приложение завершило работу.");
        }
    }

    private static void runApp() {
        while (true) {
            System.out.println("\n1. Список уроков");
            System.out.println("2. Начать урок");
            System.out.println("3. Результаты");
            System.out.println("4. Добавить текстовый урок");
            System.out.println("5. Редактировать текстовый урок");
            System.out.println("6. Удалить урок");
            System.out.println("0. Выход");
            System.out.print("Ваш выбор: ");

            String cmd = scanner.nextLine().trim();
            switch (cmd) {
                case "1" -> displayLessons();
                case "2" -> selectAndRun();
                case "3" -> displayResults();
                case "4" -> addTextLesson();
                case "5" -> editTextLesson();
                case "6" -> deleteLesson();
                case "0" -> { return; }
                default  -> System.out.println("Неверный выбор, в меню только 6 пунктов.");
            }
        }
    }

    private static void displayLessons() {
        List<Lesson> lessons = repo.getLessons();
        if (lessons.isEmpty()) {
            System.out.println("Уроков нет.");
            return;
        }
        for (int i = 0; i < lessons.size(); i++) {
            Lesson l = lessons.get(i);
            System.out.println((i + 1) + ". " + lessons.get(i));
        }
    }

    private static void selectAndRun() {
        List<Lesson> lessons = repo.getLessons();
        if (lessons.isEmpty()) {
            System.out.println("Уроков нет.");
            return;
        }
        displayLessons();
        System.out.print("Введите номер урока: ");
        int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
        if (idx < 0 || idx >= lessons.size()) {
            System.out.println("Некорректный номер.");
            return;
        }
        Lesson lesson = repo.findLessonById(lessons.get(idx).getId());
        if (lesson == null) {
            System.out.println("Урок не найден.");
            return;
        }
        runLesson(lesson);
    }

    private static void runLesson(Lesson lesson) {
        System.out.println("\n--- " + lesson.getTitle() + " (" + lesson.getType() + ") ---");
        lesson.start();

        BigDecimal score;

        if ("Тест".equals(lesson.getType())) {
            TestLesson t = (TestLesson) lesson;

            t.showQuestions();

            System.out.print("Показать ответы? (да/нет): ");
            if ("да".equalsIgnoreCase(scanner.nextLine().trim())) {
                System.out.println(t.getMaterials());
            }

            List<String> userAnswers = new ArrayList<>();
            System.out.println("Введите ответы (пустая строка — конец):");
            while (true) {
                System.out.printf("Ответ %d: ", userAnswers.size() + 1);
                String ans = scanner.nextLine();
                if (ans.isBlank()) break;
                userAnswers.add(ans);
            }

            double percent = t.grade(userAnswers);
            score = BigDecimal.valueOf(percent).setScale(2, RoundingMode.HALF_UP);
            System.out.println("Результат теста: " + score + "%");

        } else if ("Текст".equals(lesson.getType())) {
            TextLesson txt = (TextLesson) lesson;

            txt.showTextDetails();

            System.out.print("\nНажмите Enter после прочтения...");
            scanner.nextLine();

            Duration actual = Duration.between(lesson.getStartTime(), LocalDateTime.now());
            BigDecimal speed = txt.calculateSpeed(actual).setScale(2, RoundingMode.HALF_UP);
            score = speed;
            System.out.println("Скорость чтения: " + speed + " слов/мин");

        } else {
            System.out.println("Неизвестный тип урока.");
            return;
        }

        double recSec = lesson.end();
        Duration recordDuration = Duration.ofMillis((long)(recSec * 1000));
        saveResult(lesson, recordDuration, score);
    }

    private static void addTextLesson() {
        System.out.println("\n--- Добавление текстового урока ---");
        System.out.print("Название: ");
        String title = scanner.nextLine().trim();
        System.out.print("Язык: ");
        String language = scanner.nextLine().trim();
        System.out.print("Сложность: ");
        String difficulty = scanner.nextLine().trim();
        System.out.print("Текст: ");
        String text = scanner.nextLine().trim();
        System.out.print("Перевод: ");
        String translation = scanner.nextLine().trim();
        System.out.print("Сложные слова (через ;): ");
        String complexWords = scanner.nextLine().trim();
        System.out.print("Аннотация: ");
        String annotation = scanner.nextLine().trim();

        if (title.isEmpty() || language.isEmpty() || difficulty.isEmpty()
                || text.isEmpty() || complexWords.isEmpty() || annotation.isEmpty()) {
            System.out.println("Ошибка: все поля обязательны. Урок не сохранён.");
            return;
        }

        TextLesson lesson = new TextLesson();
        lesson.setTitle(title);
        lesson.setLanguage(language);
        lesson.setDifficulty(difficulty);
        lesson.setTextContent(text);
        lesson.setTranslation(translation);
        lesson.setComplexWords(complexWords);
        lesson.setAnnotation(annotation);

        repo.saveLesson(lesson);
        System.out.println("Текстовый урок успешно добавлен.");
    }

    private static void editTextLesson() {
        List<Lesson> all = repo.getLessons().stream()
                .filter(l -> "Текст".equals(l.getType()))
                .toList();
        if (all.isEmpty()) {
            System.out.println("Нет текстовых уроков для редактирования.");
            return;
        }
        System.out.println("\n--- Редактирование текстового урока ---");
        for (int i = 0; i < all.size(); i++) {
            System.out.println((i + 1) + ". " + all.get(i));
        }
        System.out.print("Номер урока: ");
        int idx;
        try {
            idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Неверный номер.");
            return;
        }
        if (idx < 0 || idx >= all.size()) {
            System.out.println("Неверный номер.");
            return;
        }
        TextLesson lesson = (TextLesson) repo.findLessonById(all.get(idx).getId());
        if (lesson == null) {
            System.out.println("Урок не найден.");
            return;
        }

        System.out.println("Оставьте пустым, чтобы не изменять.");
        System.out.printf("Название (%s): ", lesson.getTitle());
        String s = scanner.nextLine().trim();
        if (!s.isEmpty()) lesson.setTitle(s);

        System.out.printf("Язык (%s): ", lesson.getLanguage());
        s = scanner.nextLine().trim();
        if (!s.isEmpty()) lesson.setLanguage(s);

        System.out.printf("Сложность (%s): ", lesson.getDifficulty());
        s = scanner.nextLine().trim();
        if (!s.isEmpty()) lesson.setDifficulty(s);

        System.out.printf("Текст (%s): ", lesson.getTextContent());
        s = scanner.nextLine().trim();
        if (!s.isEmpty()) lesson.setTextContent(s);

        System.out.printf("Перевод (%s): ", lesson.getTranslation());
        s = scanner.nextLine().trim();
        if (!s.isEmpty()) lesson.setTranslation(s);

        System.out.printf("Сложные слова (%s): ", lesson.getComplexWordsAsArray() == null
                ? ""
                : String.join(";", lesson.getComplexWordsAsArray()));
        s = scanner.nextLine().trim();
        if (!s.isEmpty()) lesson.setComplexWords(s);

        System.out.printf("Аннотация (%s): ", lesson.getAnnotation());
        s = scanner.nextLine().trim();
        if (!s.isEmpty()) lesson.setAnnotation(s);

        if (lesson.getTitle().isEmpty() || lesson.getLanguage().isEmpty() ||
                lesson.getDifficulty().isEmpty() || lesson.getTextContent().isEmpty() ||
                lesson.getComplexWords() == null || lesson.getAnnotation() == null) {
            System.out.println("Ошибка: некоторые обязательные поля пусты. Изменения не сохранены.");
            return;
        }

        repo.updateLesson(lesson);
        System.out.println("Урок успешно обновлён.");
    }

    private static void deleteLesson() {
        List<Lesson> lessons = repo.getLessons();
        if (lessons.isEmpty()) {
            System.out.println("Нет уроков для удаления.");
            return;
        }
        displayLessons();
        System.out.print("Введите номер для удаления: ");
        int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
        if (idx < 0 || idx >= lessons.size()) {
            System.out.println("Некорректный номер.");
            return;
        }
        repo.deleteLesson(lessons.get(idx).getId());
        System.out.println("Урок удалён.");
    }

    private static void saveResult(Lesson lesson, Duration duration, BigDecimal score) {
        LessonResult r = new LessonResult();
        r.setLesson(lesson);
        r.setDuration(BigDecimal.valueOf(duration.toMillis() / 1000.0).setScale(2, RoundingMode.HALF_UP));
        r.setResult(score.setScale(2, RoundingMode.HALF_UP));
        repo.saveLessonResult(r);
    }
    private static void displayResults() {
        var list = repo.getLessonResults();
        if (list.isEmpty()) {
            System.out.println("Результатов нет.");
            return;
        }
        for (LessonResult r : list) {
            System.out.println(r);
        }
    }
}
