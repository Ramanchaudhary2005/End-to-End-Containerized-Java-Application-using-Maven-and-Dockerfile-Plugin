package com.example.studentapp.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class Student {

    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name can be at most 50 characters")
    private String name;

    @NotBlank(message = "Course is required")
    @Size(max = 50, message = "Course can be at most 50 characters")
    private String course;

    @NotBlank(message = "Student number is required")
    @Size(max = 20, message = "Student number can be at most 20 characters")
    private String studentNumber;

    @NotBlank(message = "Email is required")
    @Pattern(regexp = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$", message = "Enter a valid email address")
    private String email;

    @NotBlank(message = "Year level is required")
    private String yearLevel;

    private String gender;

    public Student() {
    }

    public Student(String name, String course, String studentNumber, String email, String yearLevel, String gender) {
        this.name = name;
        this.course = course;
        this.studentNumber = studentNumber;
        this.email = email;
        this.yearLevel = yearLevel;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(String yearLevel) {
        this.yearLevel = yearLevel;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
