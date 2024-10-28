package pl.edu.agh.iisg.to.service;

import pl.edu.agh.iisg.to.dao.CourseDao;
import pl.edu.agh.iisg.to.dao.GradeDao;
import pl.edu.agh.iisg.to.dao.StudentDao;
import pl.edu.agh.iisg.to.model.Course;
import pl.edu.agh.iisg.to.model.Grade;
import pl.edu.agh.iisg.to.model.Student;
import pl.edu.agh.iisg.to.repository.StudentRepository;
import pl.edu.agh.iisg.to.session.TransactionService;

import java.util.*;

public class SchoolService {

    private final TransactionService transactionService;

    private final StudentRepository studentRepository;
    private final GradeDao gradeDao;

    public SchoolService(TransactionService transactionService, StudentRepository studentRepository, GradeDao gradeDao) {
        this.transactionService = transactionService;
        this.studentRepository = studentRepository;
        this.gradeDao = gradeDao;
    }

    public boolean enrollStudent(final Course course, final Student student) {
        return transactionService.doAsTransaction(() -> {
            if(course.studentSet().contains(student)){
                return false;
            }
           course.studentSet().add(student);
           student.courseSet().add(course);
           return true;
        }).orElse(false);
    }

    public boolean removeStudent(int indexNumber) {
        Optional<Student> optionalStudent = studentRepository.studentDao().findByIndexNumber(indexNumber);

        if(optionalStudent.isPresent()){
            Student student = optionalStudent.get();

            for (Course course : student.courseSet()) {
                course.studentSet().remove(student);
            }

            studentRepository.studentDao().remove(student);

            return true;
        }
        return false;
    }

    public boolean gradeStudent(final Student student, final Course course, final float gradeValue) {
        return transactionService.doAsTransaction(() -> {

            Grade newGrade = new Grade(student, course, gradeValue);

            gradeDao.currentSession().persist(newGrade);

            course.gradeSet().add(newGrade);
            student.gradeSet().add(newGrade);

            return true;
        }).orElse(false);
    }

    public Map<String, List<Float>> getStudentGrades(String courseName) {
        Map<String, List<Float>> gradeMap = new HashMap<>();
        Optional<Course> optionalCourse = studentRepository.courseDao().findByName(courseName);

        if(optionalCourse.isPresent()){
            Course course = optionalCourse.get();

            Set<Grade> gradeSet = course.gradeSet();

            for(Grade grade: gradeSet){
                addValueToMap(gradeMap, String.format("%s %s", grade.student().firstName(), grade.student().lastName()),
                        grade.grade());
            }

            return gradeMap;
        }
        return Collections.emptyMap();
    }

    public static void addValueToMap(Map<String, List<Float>> map, String key, Float value) {
        if (!map.containsKey(key)) {
            map.put(key, new ArrayList<>());
        }

        map.get(key).add(value);
    }
}
