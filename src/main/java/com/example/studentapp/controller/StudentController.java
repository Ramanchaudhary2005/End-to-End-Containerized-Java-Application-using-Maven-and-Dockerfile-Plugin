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
        model.addAttribute("student", new Student());
        model.addAttribute("students", studentService.getAllStudents());
        return "index";
    }

    @PostMapping("/students")
    public String addStudent(@Valid @ModelAttribute("student") Student student,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("students", studentService.getAllStudents());
            return "index";
        }

        studentService.addStudent(student);
        return "redirect:/";
    }
}
