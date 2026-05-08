package com.example.studentapp.repository;

import com.example.studentapp.model.Mark;
import com.example.studentapp.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarkRepository extends JpaRepository<Mark, Long> {
    List<Mark> findByStudent(Student student);
}
