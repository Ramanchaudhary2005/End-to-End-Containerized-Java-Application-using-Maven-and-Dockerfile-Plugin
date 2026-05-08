package com.example.studentapp.controller;

import com.example.studentapp.model.Student;
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

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/")
    public String showDashboard(Model model) {
        populateCommonModel(model, "dashboard");
        return "index";
    }

    @GetMapping("/dashboard")
    public String showDashboardPage(Model model) {
        populateCommonModel(model, "dashboard");
        return "index";
    }

    @GetMapping("/students")
    public String showStudentsPage(Model model) {
        populateCommonModel(model, "students");
        return "index";
    }

    @GetMapping("/courses")
    public String showCoursesPage(Model model) {
        populateCommonModel(model, "courses");
        return "index";
    }

    @GetMapping("/results")
    public String showResultsPage(Model model) {
        populateCommonModel(model, "results");
        return "index";
    }

    @GetMapping("/settings")
    public String showSettingsPage(Model model) {
        populateCommonModel(model, "settings");
        return "index";
    }

    @PostMapping("/students")
    public String addStudent(@Valid @ModelAttribute("student") Student student,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            populateCommonModel(model, "students");
            return "index";
        }

        studentService.addStudent(student);
        return "redirect:/students";
    }

    private void populateCommonModel(Model model, String activePage) {
        model.addAttribute("student", new Student());
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("activePage", activePage);
    }
}
