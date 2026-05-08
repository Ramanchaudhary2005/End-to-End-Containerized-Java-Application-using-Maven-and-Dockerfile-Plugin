package com.example.studentapp.controller;

import com.example.studentapp.model.Mark;
import com.example.studentapp.model.Student;
import com.example.studentapp.repository.UserAccountRepository;
import com.example.studentapp.service.StudentService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class StudentPortalController {
    private final UserAccountRepository userAccountRepository;
    private final StudentService studentService;

    public StudentPortalController(UserAccountRepository userAccountRepository, StudentService studentService) {
        this.userAccountRepository = userAccountRepository;
        this.studentService = studentService;
    }

    @GetMapping("/student/dashboard")
    public String studentDashboard(Authentication authentication, Model model) {
        var user = userAccountRepository.findByUsername(authentication.getName()).orElse(null);
        if (user == null || user.getStudent() == null) {
            model.addAttribute("errorMessage", "No student profile is linked to this account.");
            return "student-dashboard";
        }

        Student student = user.getStudent();
        List<Mark> marks = studentService.getStudentMarks(student);
        model.addAttribute("student", student);
        model.addAttribute("marks", marks);
        model.addAttribute("attendanceRows", studentService.getStudentAttendance(student));
        model.addAttribute("attendanceRate", studentService.getAttendanceRate(student));
        model.addAttribute("overallAverage", marks.stream().mapToDouble(Mark::getScore).average().orElse(0));

        Map<String, Double> subjectAvg = marks.stream()
                .collect(Collectors.groupingBy(mark -> mark.getSubject().getName(),
                        Collectors.averagingDouble(Mark::getScore)));
        model.addAttribute("subjectAverages", subjectAvg);
        model.addAttribute("subjectLabels", String.join(",", subjectAvg.keySet()));
        model.addAttribute("subjectScores", subjectAvg.values().stream()
                .map(v -> String.valueOf(Math.round(v * 100.0) / 100.0))
                .collect(Collectors.joining(",")));
        return "student-dashboard";
    }
}
