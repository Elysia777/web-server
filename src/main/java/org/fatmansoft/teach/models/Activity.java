package org.fatmansoft.teach.models;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(	name = "activity",
        uniqueConstraints = {
        })

public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer activityId;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "activity_id")
    private List<StudentActivity> studentActivity;
    private String activityName;
    private String type;
    private String date;
    private String time;
    private String organizer;
    private String people;//活动人数名额
    private String remainPeople;//活动剩余名额;
    private String activityAddress;
    private String enrollFee;

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public String getRemainPeople() {
        return remainPeople;
    }

    public void setRemainPeople(String remainPeople) {
        this.remainPeople = remainPeople;
    }

    public String getActivityAddress() {
        return activityAddress;
    }

    public void setActivityAddress(String activityAddress) {
        this.activityAddress = activityAddress;
    }

    public String getEnrollFee() {
        return enrollFee;
    }

    public void setEnrollFee(String enrollFee) {
        this.enrollFee = enrollFee;
    }


    public List<StudentActivity> getStudentActivity() {
        return studentActivity;
    }

    public void setStudentActivity(List<StudentActivity> studentActivity) {
        this.studentActivity = studentActivity;
    }
}
