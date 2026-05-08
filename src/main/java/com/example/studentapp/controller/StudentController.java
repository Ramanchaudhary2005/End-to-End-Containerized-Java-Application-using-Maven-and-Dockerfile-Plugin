package com.example.studentapp.controller;

import com.example.studentapp.model.AttendanceEntryForm;
import com.example.studentapp.model.AttendanceStatus;
import com.example.studentapp.model.MarkEntryForm;
import com.example.studentapp.model.Student;
import com.example.studentapp.repository.UserAccountRepository;
import com.example.studentapp.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class StudentController {
    private final StudentService studentService;
    private final UserAccountRepository userAccountRepository;

    public StudentController(StudentService studentService, UserAccountRepository userAccountRepository) {
        this.studentService = studentService;
        this.userAccountRepository = userAccountRepository;
    }

    @GetMapping("/teacher/dashboard")
    public String showTeacherDashboard(Model model) {
        populateTeacherModel(model);
        return "teacher-dashboard";
    }

    @PostMapping("/teacher/students")
    public String addStudent(@Valid @ModelAttribute("student") Student student,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            populateTeacherModel(model);
            return "teacher-dashboard";
        }
        studentService.addStudent(student);
        return "redirect:/teacher/dashboard";
    }

    @PostMapping("/teacher/marks")
    public String uploadMarks(@ModelAttribute("markForm") MarkEntryForm markForm) {
        studentService.uploadMark(markForm);
        return "redirect:/teacher/dashboard";
    }

    @PostMapping("/teacher/attendance")
    public String addAttendance(@ModelAttribute("attendanceForm") AttendanceEntryForm attendanceForm) {
        studentService.recordAttendance(attendanceForm);
        return "redirect:/teacher/dashboard";
    }

    private void populateTeacherModel(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("subjects", studentService.getAllSubjects());
        model.addAttribute("marks", studentService.getAllMarks());
        model.addAttribute("attendanceRows", studentService.getAllAttendance());
        model.addAttribute("markForm", new MarkEntryForm());
        AttendanceEntryForm attendanceEntryForm = new AttendanceEntryForm();
        attendanceEntryForm.setStatus(AttendanceStatus.PRESENT);
        model.addAttribute("attendanceForm", attendanceEntryForm);
        model.addAttribute("classTopper", studentService.getClassTopper());
        model.addAttribute("subjectToppers", studentService.getSubjectToppers());
        model.addAttribute("subjectAverages", studentService.getAverageScoreBySubject());
        model.addAttribute("topStudents", studentService.getTopStudentsByAverage(5));
        model.addAttribute("teacherUserCount", userAccountRepository.count());
    }
}
