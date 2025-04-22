package lab3.repository;

import lab3.entity.Lesson;
import lab3.entity.LessonResult;
import lab3.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class LessonRepository {

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public List<Lesson> getLessons() {
        List<Lesson> list = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            int id = 1;
            while (true) {
                Lesson l = session.get(Lesson.class, id);
                if (l == null) break;
                list.add(l);
                id++;
            }
        }
        return list;
    }

    public Lesson findLessonById(int lessonId) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Lesson.class, lessonId);
        }
    }

    public void saveLesson(Lesson lesson) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(lesson);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Ошибка при сохранении урока", e);
        }
    }

    public void updateLesson(Lesson lesson) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.merge(lesson);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Ошибка при обновлении урока", e);
        }
    }

    public void deleteLesson(int lessonId) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            Lesson lesson = session.get(Lesson.class, lessonId);
            if (lesson != null) session.delete(lesson);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Ошибка при удалении урока", e);
        }
    }

    public List<LessonResult> getLessonResults() {
        List<LessonResult> list = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            int id = 1;
            while (true) {
                LessonResult r = session.get(LessonResult.class, id);
                if (r == null) break;
                list.add(r);
                id++;
            }
        }
        return list;
    }

    public void saveLessonResult(LessonResult result) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(result);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Не удалось сохранить результат урока", e);
        }
    }
}
