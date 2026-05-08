package com.example.studentapp.repository;

import com.example.studentapp.model.Attendance;
import com.example.studentapp.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudent(Student student);
}
