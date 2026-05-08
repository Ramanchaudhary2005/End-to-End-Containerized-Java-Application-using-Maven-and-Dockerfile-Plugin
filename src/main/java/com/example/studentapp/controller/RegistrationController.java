package com.example.studentapp.controller;

import com.example.studentapp.model.RegistrationForm;
import com.example.studentapp.model.Student;
import com.example.studentapp.model.TeacherProfile;
import com.example.studentapp.model.UserAccount;
import com.example.studentapp.model.UserRole;
import com.example.studentapp.repository.StudentRepository;
import com.example.studentapp.repository.TeacherProfileRepository;
import com.example.studentapp.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.Year;

@Controller
public class RegistrationController {
    private final StudentRepository studentRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(StudentRepository studentRepository,
                                  TeacherProfileRepository teacherProfileRepository,
                                  UserAccountRepository userAccountRepository,
                                  PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.teacherProfileRepository = teacherProfileRepository;
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("form", new RegistrationForm());
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@ModelAttribute("form") RegistrationForm form, Model model) {
        if (form.getRole() == null || form.getFullName() == null || form.getEmail() == null) {
            model.addAttribute("error", "Please fill all required details.");
            return "register";
        }
        if (form.getPassword() == null || !form.getPassword().equals(form.getConfirmPassword())) {
            model.addAttribute("error", "Passwords do not match.");
            return "register";
        }
        if (form.getPassword().length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters.");
            return "register";
        }

        String role = form.getRole().trim().toUpperCase();
        String regNo;

        if ("TEACHER".equals(role)) {
            TeacherProfile teacher = teacherProfileRepository.findByEmail(form.getEmail()).orElse(null);
            if (teacher == null) {
                teacher = teacherProfileRepository.save(new TeacherProfile(form.getFullName(), form.getEmail()));
            }

            regNo = generateRegNo("TCH", teacher.getId());
            if (userAccountRepository.findByUsername(regNo).isPresent()) {
                model.addAttribute("error", "Registration already exists. Please login.");
                return "register";
            }

            UserAccount account = new UserAccount();
            account.setUsername(regNo);
            account.setPassword(passwordEncoder.encode(form.getPassword()));
            account.setRole(UserRole.ROLE_TEACHER);
            account.setTeacher(teacher);
            userAccountRepository.save(account);
        } else {
            // STUDENT
            if (isBlank(form.getCourse()) || isBlank(form.getYearLevel())) {
                model.addAttribute("error", "Course and Year Level are required for students.");
                return "register";
            }
            Student student = new Student(
                    form.getFullName(),
                    form.getCourse(),
                    "TEMP",
                    form.getEmail(),
                    form.getYearLevel(),
                    form.getGender()
            );
            student = studentRepository.save(student);

            regNo = generateRegNo("STU", student.getId());
            student.setStudentNumber(regNo);
            studentRepository.save(student);

            UserAccount account = new UserAccount();
            account.setUsername(regNo);
            account.setPassword(passwordEncoder.encode(form.getPassword()));
            account.setRole(UserRole.ROLE_STUDENT);
            account.setStudent(student);
            userAccountRepository.save(account);
        }

        model.addAttribute("registrationNumber", regNo);
        return "register-success";
    }

    private String generateRegNo(String prefix, Long id) {
        int year = Year.now().getValue();
        return prefix + "-" + year + "-" + String.format("%05d", id);
    }

    private boolean isBlank(String v) {
        return v == null || v.trim().isEmpty();
    }
}
