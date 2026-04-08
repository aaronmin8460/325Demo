package common.model;

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

    public void addStudent() {

        // TODO: Implement add student logic

    }

    public void removeStudent() {

        // TODO: Implement remove student logic

    }

    // getters and setters

    public int getClassId() { return classId; }

    public void setClassId(int classId) { this.classId = classId; }

    public String getClassName() { return className; }

    public void setClassName(String className) { this.className = className; }

    public List<Student> getStudents() { return students; }

    public void setStudents(List<Student> students) { this.students = students; }

}