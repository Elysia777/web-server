package org.fatmansoft.teach.models;

import javax.persistence.*;

@Entity
@Table(	name = "studentActivity",
        uniqueConstraints = {
        })
public class StudentActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer studentActivityId;
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;


    public Integer getStudentActivityId() {
        return studentActivityId;
    }

    public void setStudentActivityId(Integer studentActivityId) {
        this.studentActivityId = studentActivityId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

}
