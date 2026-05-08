package com.example.studentapp.config;

import com.example.studentapp.model.Student;
import com.example.studentapp.model.Subject;
import com.example.studentapp.model.UserAccount;
import com.example.studentapp.model.UserRole;
import com.example.studentapp.repository.StudentRepository;
import com.example.studentapp.repository.SubjectRepository;
import com.example.studentapp.repository.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(SubjectRepository subjectRepository,
                           StudentRepository studentRepository,
                           UserAccountRepository userAccountRepository,
                           PasswordEncoder passwordEncoder) {
        this.subjectRepository = subjectRepository;
        this.studentRepository = studentRepository;
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (subjectRepository.count() == 0) {
            subjectRepository.saveAll(List.of(
                    new Subject("Mathematics"),
                    new Subject("Physics"),
                    new Subject("Chemistry"),
                    new Subject("Computer Science"),
                    new Subject("English")
            ));
        }
    }
}
