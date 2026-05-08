package com.example.studentapp.service;

import com.example.studentapp.model.Attendance;
import com.example.studentapp.model.AttendanceEntryForm;
import com.example.studentapp.model.Mark;
import com.example.studentapp.model.MarkEntryForm;
import com.example.studentapp.model.Student;
import com.example.studentapp.model.Subject;
import com.example.studentapp.repository.AttendanceRepository;
import com.example.studentapp.repository.MarkRepository;
import com.example.studentapp.repository.StudentRepository;
import com.example.studentapp.repository.SubjectRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final MarkRepository markRepository;
    private final AttendanceRepository attendanceRepository;

    public StudentService(StudentRepository studentRepository,
                          SubjectRepository subjectRepository,
                          MarkRepository markRepository,
                          AttendanceRepository attendanceRepository) {
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.markRepository = markRepository;
        this.attendanceRepository = attendanceRepository;
    }

    public void addStudent(Student student) {
        studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public void uploadMark(MarkEntryForm form) {
        Optional<Student> student = studentRepository.findById(form.getStudentId());
        Optional<Subject> subject = subjectRepository.findById(form.getSubjectId());
        if (student.isEmpty() || subject.isEmpty()) {
            return;
        }
        Mark mark = new Mark();
        mark.setStudent(student.get());
        mark.setSubject(subject.get());
        mark.setExamType(form.getExamType());
        mark.setScore(form.getScore());
        markRepository.save(mark);
    }

    public void recordAttendance(AttendanceEntryForm form) {
        Optional<Student> student = studentRepository.findById(form.getStudentId());
        Optional<Subject> subject = subjectRepository.findById(form.getSubjectId());
        if (student.isEmpty() || subject.isEmpty() || form.getAttendanceDate() == null || form.getStatus() == null) {
            return;
        }
        Attendance attendance = new Attendance();
        attendance.setStudent(student.get());
        attendance.setSubject(subject.get());
        attendance.setAttendanceDate(form.getAttendanceDate());
        attendance.setStatus(form.getStatus());
        attendanceRepository.save(attendance);
    }

    public List<Mark> getAllMarks() {
        return markRepository.findAll();
    }

    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    public List<Mark> getStudentMarks(Student student) {
        return markRepository.findByStudent(student);
    }

    public List<Attendance> getStudentAttendance(Student student) {
        return attendanceRepository.findByStudent(student);
    }

    public double getAttendanceRate(Student student) {
        List<Attendance> rows = attendanceRepository.findByStudent(student);
        if (rows.isEmpty()) {
            return 0;
        }
        long presentCount = rows.stream().filter(a -> "PRESENT".equals(a.getStatus().name())).count();
        return (presentCount * 100.0) / rows.size();
    }

    public Map<String, Double> getAverageScoreBySubject() {
        Map<String, List<Double>> grouped = new HashMap<>();
        for (Mark mark : markRepository.findAll()) {
            grouped.computeIfAbsent(mark.getSubject().getName(), key -> new java.util.ArrayList<>()).add(mark.getScore());
        }
        Map<String, Double> averages = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : grouped.entrySet()) {
            double avg = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0);
            averages.put(entry.getKey(), Math.round(avg * 100.0) / 100.0);
        }
        return averages;
    }

    public String getClassTopper() {
        Map<String, Double> totals = new HashMap<>();
        for (Mark mark : markRepository.findAll()) {
            totals.merge(mark.getStudent().getName(), mark.getScore(), Double::sum);
        }
        return totals.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No data");
    }

    public Map<String, String> getSubjectToppers() {
        Map<String, List<Mark>> bySubject = markRepository.findAll().stream()
                .collect(Collectors.groupingBy(mark -> mark.getSubject().getName()));
        Map<String, String> toppers = new HashMap<>();
        for (Map.Entry<String, List<Mark>> entry : bySubject.entrySet()) {
            String topper = entry.getValue().stream()
                    .max(Comparator.comparingDouble(Mark::getScore))
                    .map(mark -> mark.getStudent().getName() + " (" + mark.getScore() + ")")
                    .orElse("No data");
            toppers.put(entry.getKey(), topper);
        }
        return toppers;
    }

    public List<Student> getTopStudentsByAverage(int limit) {
        Map<Student, Double> avgMap = new HashMap<>();
        List<Mark> marks = markRepository.findAll();
        for (Student student : studentRepository.findAll()) {
            double avg = marks.stream()
                    .filter(mark -> mark.getStudent().getId().equals(student.getId()))
                    .mapToDouble(Mark::getScore)
                    .average()
                    .orElse(0);
            avgMap.put(student, avg);
        }
        return avgMap.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();
    }
}
