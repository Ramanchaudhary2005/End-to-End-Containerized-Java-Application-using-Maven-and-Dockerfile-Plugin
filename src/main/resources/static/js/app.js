const STORAGE_KEY = 'teacherPortalData';
const subjects = ['Mathematics', 'Physics', 'Chemistry', 'Computer Science', 'English'];
let charts = {};

const demoData = {
    students: [
        { id: crypto.randomUUID(), name: 'Anaya Sharma', studentNumber: 'STU-001', email: 'anaya@example.com', course: 'Computer Science', yearLevel: '2nd Year', gender: 'Female' },
        { id: crypto.randomUUID(), name: 'Kabir Mehta', studentNumber: 'STU-002', email: 'kabir@example.com', course: 'Computer Science', yearLevel: '2nd Year', gender: 'Male' },
        { id: crypto.randomUUID(), name: 'Meera Rao', studentNumber: 'STU-003', email: 'meera@example.com', course: 'Computer Science', yearLevel: '2nd Year', gender: 'Female' }
    ],
    marks: [],
    attendance: []
};

function seedDemoData() {
    const [anaya, kabir, meera] = demoData.students;
    demoData.marks = [
        mark(anaya.id, 'Mathematics', 'Midterm', 94),
        mark(anaya.id, 'Physics', 'Midterm', 88),
        mark(anaya.id, 'Computer Science', 'Final', 96),
        mark(kabir.id, 'Mathematics', 'Midterm', 82),
        mark(kabir.id, 'Physics', 'Midterm', 91),
        mark(kabir.id, 'Computer Science', 'Final', 86),
        mark(meera.id, 'Mathematics', 'Midterm', 89),
        mark(meera.id, 'Physics', 'Midterm', 84),
        mark(meera.id, 'Computer Science', 'Final', 92)
    ];
    demoData.attendance = [
        attendance(anaya.id, 'Mathematics', 'PRESENT'),
        attendance(anaya.id, 'Physics', 'PRESENT'),
        attendance(kabir.id, 'Mathematics', 'LATE'),
        attendance(kabir.id, 'Physics', 'PRESENT'),
        attendance(meera.id, 'Mathematics', 'PRESENT'),
        attendance(meera.id, 'Physics', 'ABSENT')
    ];
    saveData(demoData);
}

function mark(studentId, subject, examType, score) {
    return { id: crypto.randomUUID(), studentId, subject, examType, score };
}

function attendance(studentId, subject, status) {
    return { id: crypto.randomUUID(), studentId, subject, date: new Date().toISOString().slice(0, 10), status };
}

function loadData() {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) {
        seedDemoData();
        return JSON.parse(localStorage.getItem(STORAGE_KEY));
    }
    return JSON.parse(raw);
}

function saveData(data) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(data));
}

function getStudent(data, id) {
    return data.students.find((student) => student.id === id);
}

function average(values) {
    if (values.length === 0) {
        return 0;
    }
    return round(values.reduce((sum, value) => sum + Number(value), 0) / values.length);
}

function round(value) {
    return Math.round(value * 100) / 100;
}

function gradeFor(score) {
    if (score >= 90) return 'A+';
    if (score >= 80) return 'A';
    if (score >= 70) return 'B';
    if (score >= 60) return 'C';
    if (score >= 50) return 'D';
    return 'Needs Support';
}

function studentAverage(data, studentId) {
    return average(data.marks.filter((entry) => entry.studentId === studentId).map((entry) => entry.score));
}

function attendanceRate(data, studentId) {
    const rows = data.attendance.filter((entry) => entry.studentId === studentId);
    if (rows.length === 0) {
        return 0;
    }
    const present = rows.filter((entry) => entry.status === 'PRESENT').length;
    return round((present * 100) / rows.length);
}

function subjectAverages(data) {
    return subjects.reduce((result, subject) => {
        const scores = data.marks.filter((entry) => entry.subject === subject).map((entry) => entry.score);
        if (scores.length > 0) {
            result[subject] = average(scores);
        }
        return result;
    }, {});
}

function subjectToppers(data) {
    return subjects.map((subject) => {
        const best = data.marks
            .filter((entry) => entry.subject === subject)
            .sort((a, b) => b.score - a.score)[0];
        return {
            subject,
            label: best ? `${getStudent(data, best.studentId)?.name || 'Unknown'} (${best.score})` : 'No data'
        };
    });
}

function studentAverages(data) {
    return data.students
        .map((student) => ({ name: student.name, average: studentAverage(data, student.id) }))
        .sort((a, b) => b.average - a.average)
        .reduce((result, entry) => {
            result[entry.name] = entry.average;
            return result;
        }, {});
}

function attendanceRates(data) {
    return data.students.reduce((result, student) => {
        result[student.name] = attendanceRate(data, student.id);
        return result;
    }, {});
}

function renderSelects(data) {
    const studentOptions = ['<option value="">Select student</option>']
        .concat(data.students.map((student) => `<option value="${student.id}">${student.name} (${student.studentNumber})</option>`))
        .join('');
    document.getElementById('markStudent').innerHTML = studentOptions;
    document.getElementById('attendanceStudent').innerHTML = studentOptions;

    const subjectOptions = ['<option value="">Select subject</option>']
        .concat(subjects.map((subject) => `<option value="${subject}">${subject}</option>`))
        .join('');
    document.getElementById('markSubject').innerHTML = subjectOptions;
    document.getElementById('attendanceSubject').innerHTML = subjectOptions;
}

function renderMetrics(data) {
    const averages = studentAverages(data);
    const topper = Object.entries(averages).sort((a, b) => b[1] - a[1])[0];
    document.getElementById('studentCount').textContent = data.students.length;
    document.getElementById('markCount').textContent = data.marks.length;
    document.getElementById('attendanceCount').textContent = data.attendance.length;
    document.getElementById('classTopper').textContent = topper && topper[1] > 0 ? topper[0] : 'No data';
}

function renderTables(data) {
    const studentRows = document.getElementById('studentRows');
    studentRows.innerHTML = data.students.length
        ? data.students.map((student) => `
            <tr>
                <td>${student.name}</td>
                <td>${student.studentNumber}</td>
                <td>${student.course}</td>
                <td>${student.yearLevel}</td>
                <td>${student.email}</td>
                <td><button class="danger-button" data-delete-student="${student.id}" type="button">Delete</button></td>
            </tr>
        `).join('')
        : '<tr><td colspan="6">No students added yet.</td></tr>';

    const markRows = document.getElementById('markRows');
    markRows.innerHTML = data.marks.length
        ? data.marks.slice().reverse().map((entry) => `
            <tr>
                <td>${getStudent(data, entry.studentId)?.name || 'Deleted student'}</td>
                <td>${entry.subject}</td>
                <td>${entry.examType}</td>
                <td>${entry.score}</td>
                <td><button class="danger-button" data-delete-mark="${entry.id}" type="button">Delete</button></td>
            </tr>
        `).join('')
        : '<tr><td colspan="5">No marks uploaded yet.</td></tr>';
}

function renderToppers(data) {
    const container = document.getElementById('subjectToppers');
    container.innerHTML = subjectToppers(data).map((entry) => `
        <div class="topper-row">
            <span>${entry.subject}</span>
            <strong>${entry.label}</strong>
        </div>
    `).join('');
}

function chart(id, type, source, color) {
    const canvas = document.getElementById(id);
    const labels = Object.keys(source);
    if (charts[id]) {
        charts[id].destroy();
    }
    charts[id] = new Chart(canvas, {
        type,
        data: {
            labels,
            datasets: [{
                data: Object.values(source),
                backgroundColor: color,
                borderColor: color,
                borderWidth: 2,
                tension: 0.35,
                fill: type === 'line'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: { y: { beginAtZero: true, max: 100 } },
            plugins: { legend: { display: false } }
        }
    });
}

function renderCharts(data) {
    chart('subjectChart', 'bar', subjectAverages(data), '#2662d9');
    chart('studentChart', 'line', studentAverages(data), '#0f8b6f');
    chart('attendanceChart', 'bar', attendanceRates(data), '#d97706');
}

function renderReports(data) {
    const container = document.getElementById('reportCards');
    container.innerHTML = data.students.length
        ? data.students.map((student) => {
            const marks = data.marks.filter((entry) => entry.studentId === student.id);
            const avg = studentAverage(data, student.id);
            const rate = attendanceRate(data, student.id);
            const markRows = marks.length
                ? marks.map((entry) => `<tr><td>${entry.subject}</td><td>${entry.examType}</td><td>${entry.score}</td></tr>`).join('')
                : '<tr><td colspan="3">No marks available.</td></tr>';
            return `
                <article class="report-card">
                    <div class="report-head">
                        <div>
                            <span>${student.studentNumber}</span>
                            <h3>${student.name}</h3>
                        </div>
                        <strong class="grade">${gradeFor(avg)}</strong>
                    </div>
                    <div class="report-stats">
                        <span>Average <strong>${avg}</strong></span>
                        <span>Attendance <strong>${rate}%</strong></span>
                    </div>
                    <table>
                        <thead><tr><th>Subject</th><th>Exam</th><th>Score</th></tr></thead>
                        <tbody>${markRows}</tbody>
                    </table>
                    <button type="button" data-download-report="${student.id}">Download PDF</button>
                </article>
            `;
        }).join('')
        : '<p class="empty-state">Add students to generate report cards.</p>';
}

function downloadReport(data, studentId) {
    const student = getStudent(data, studentId);
    const marks = data.marks.filter((entry) => entry.studentId === studentId);
    const avg = studentAverage(data, studentId);
    const rate = attendanceRate(data, studentId);
    const rows = marks.map((entry) => [entry.subject, entry.examType, String(entry.score)]);
    const { jsPDF } = window.jspdf;
    const doc = new jsPDF();

    doc.setFontSize(18);
    doc.text('Student Report Card', 14, 18);
    doc.setFontSize(11);
    doc.text(`Name: ${student.name}`, 14, 30);
    doc.text(`Student No: ${student.studentNumber}`, 14, 38);
    doc.text(`Course: ${student.course}`, 14, 46);
    doc.text(`Year: ${student.yearLevel}`, 14, 54);
    doc.text(`Average: ${avg}`, 120, 30);
    doc.text(`Attendance: ${rate}%`, 120, 38);
    doc.text(`Grade: ${gradeFor(avg)}`, 120, 46);
    doc.autoTable({
        head: [['Subject', 'Exam', 'Score']],
        body: rows.length ? rows : [['No marks available', '', '']],
        startY: 66,
        headStyles: { fillColor: [38, 98, 217] }
    });
    doc.save(`${student.studentNumber}-report-card.pdf`);
}

function render() {
    const data = loadData();
    renderSelects(data);
    renderMetrics(data);
    renderTables(data);
    renderToppers(data);
    renderCharts(data);
    renderReports(data);
}

document.getElementById('studentForm').addEventListener('submit', (event) => {
    event.preventDefault();
    const data = loadData();
    const formData = new FormData(event.currentTarget);
    data.students.push({
        id: crypto.randomUUID(),
        name: formData.get('name').trim(),
        studentNumber: formData.get('studentNumber').trim(),
        email: formData.get('email').trim(),
        course: formData.get('course').trim(),
        yearLevel: formData.get('yearLevel').trim(),
        gender: formData.get('gender')
    });
    saveData(data);
    event.currentTarget.reset();
    render();
});

document.getElementById('markForm').addEventListener('submit', (event) => {
    event.preventDefault();
    const data = loadData();
    const formData = new FormData(event.currentTarget);
    data.marks.push(mark(
        formData.get('studentId'),
        formData.get('subject'),
        formData.get('examType').trim(),
        Number(formData.get('score'))
    ));
    saveData(data);
    event.currentTarget.reset();
    render();
});

document.getElementById('attendanceForm').addEventListener('submit', (event) => {
    event.preventDefault();
    const data = loadData();
    const formData = new FormData(event.currentTarget);
    data.attendance.push({
        id: crypto.randomUUID(),
        studentId: formData.get('studentId'),
        subject: formData.get('subject'),
        date: formData.get('date'),
        status: formData.get('status')
    });
    saveData(data);
    event.currentTarget.reset();
    render();
});

document.body.addEventListener('click', (event) => {
    const data = loadData();
    const deleteStudentId = event.target.dataset.deleteStudent;
    const deleteMarkId = event.target.dataset.deleteMark;
    const reportStudentId = event.target.dataset.downloadReport;

    if (deleteStudentId) {
        data.students = data.students.filter((student) => student.id !== deleteStudentId);
        data.marks = data.marks.filter((entry) => entry.studentId !== deleteStudentId);
        data.attendance = data.attendance.filter((entry) => entry.studentId !== deleteStudentId);
        saveData(data);
        render();
    }

    if (deleteMarkId) {
        data.marks = data.marks.filter((entry) => entry.id !== deleteMarkId);
        saveData(data);
        render();
    }

    if (reportStudentId) {
        downloadReport(data, reportStudentId);
    }
});

document.getElementById('resetDemo').addEventListener('click', () => {
    localStorage.removeItem(STORAGE_KEY);
    seedDemoData();
    render();
});

render();
