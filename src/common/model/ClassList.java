package common.model;

import java.util.ArrayList;
import java.util.List;

public class ClassList {

    private int classId;

    private String className;

    private List<Student> students;

    public ClassList(int classId, String className, List<Student> students) {

        this.classId = classId;

        this.className = className;

        this.students = students;

    }

    public void addStudent(Student student) {

        if (student == null) {

            return;

        }

        if (students == null) {

            students = new ArrayList<>();

        }

        for (Student existingStudent : students) {

            if (existingStudent.getName() != null
                    && existingStudent.getName().equalsIgnoreCase(student.getName())) {

                return;

            }

        }

        students.add(student);

    }

    public void removeStudent(String username) {

        if (students == null || username == null) {

            return;

        }

        Student studentToRemove = null;

        for (Student student : students) {

            if (student.getName() != null && student.getName().equalsIgnoreCase(username)) {

                studentToRemove = student;
                break;

            }

        }

        if (studentToRemove != null) {

            students.remove(studentToRemove);

        }

    }

    // getters and setters

    public int getClassId() { return classId; }

    public void setClassId(int classId) { this.classId = classId; }

    public String getClassName() { return className; }

    public void setClassName(String className) { this.className = className; }

    public List<Student> getStudents() { return students; }

    public void setStudents(List<Student> students) { this.students = students; }

}
