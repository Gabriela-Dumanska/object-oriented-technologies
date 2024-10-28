package pl.edu.agh.iisg.to.repository;

import pl.edu.agh.iisg.to.dao.CourseDao;
import pl.edu.agh.iisg.to.dao.StudentDao;
import pl.edu.agh.iisg.to.model.Course;
import pl.edu.agh.iisg.to.model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class StudentRepository implements Repository<Student>{

    private StudentDao studentDao;
    private CourseDao courseDao;
    public StudentRepository(StudentDao studentDao, CourseDao courseDao){
        this.studentDao = studentDao;
        this.courseDao = courseDao;
    }

    @Override
    public Optional<Student> add(Student student) {
        return studentDao.add(student);
    }

    @Override
    public Optional<Student> getById(int id) {
        return studentDao.findById(id);
    }

    @Override
    public List<Student> findAll() {
        return studentDao.findAll();
    }

    @Override
    public void remove(Student student) {
        for (Course course : student.courseSet()) {
            course.studentSet().remove(student);
        }

        studentDao.remove(student);
    }

    public List<Student> findAllByCourseName(String courseName) {
        List<Student> students = new ArrayList<>();

        Optional<Course> course = courseDao.findByName(courseName);

        try {
            for (Student student : course.orElseThrow().studentSet()) {
                students.add(student);
            }

            return students;
        } catch (NoSuchElementException e){
            System.out.println("No such course!");
        }

        return students;
    }

    public StudentDao studentDao() {
        return studentDao;
    }
    public CourseDao courseDao() {
        return courseDao;
    }
}
