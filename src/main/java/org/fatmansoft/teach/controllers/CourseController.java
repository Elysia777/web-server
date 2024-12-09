package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.*;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/course")

public class CourseController {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @PostMapping("/getCourseListSerializable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StreamingResponseBody> getCourseListSerializable(@Valid @RequestBody DataRequest dataRequest) {
        try {
            List<Course> cList = courseRepository.findAll();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
                ObjectOutputStream oo = new ObjectOutputStream(out);
                oo.writeObject(cList);
                oo.close();
                byte [] data = out.toByteArray();
                MediaType mType = new MediaType(MediaType.APPLICATION_OCTET_STREAM);
                StreamingResponseBody stream = outputStream -> {
                    outputStream.write(data);
                };
                return ResponseEntity.ok()
                        .contentType(mType)
                        .body(stream);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return ResponseEntity.internalServerError().build();
    }

    @PostMapping("/getCourseList")
    public DataResponse getCourseList(@Valid @RequestBody DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        if(numName == null)
            numName = "";
        List<Course> cList = courseRepository.findCourseListByNumName(numName);  //数据库查询操作
        List dataList = new ArrayList();
        Map m;
        Course pc;
        Teacher tc;
        for (Course c : cList) {
            m = new HashMap();
            m.put("courseId", c.getCourseId()+"");
            m.put("num",c.getNum());
            m.put("name",c.getName());
            m.put("credit",c.getCredit());
            m.put("coursePath",c.getCoursePath());
            pc =c.getPreCourse();
            if(pc != null) {
                m.put("preCourse",pc.getName());
                m.put("preCourseId",pc.getCourseId());
            }
            tc=c.getTeacher();
            if(tc!=null){
               m.put("teacherId",tc.getTeacherId());
               m.put("teacherNum",tc.getPerson().getNum());
               m.put("teacherName",tc.getPerson().getName());
            }
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }
    @PostMapping("/courseSave")
    public DataResponse courseSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        String num = dataRequest.getString("num");
        String name = dataRequest.getString("name");
        String coursePath = dataRequest.getString("coursePath");
        Integer credit = dataRequest.getInteger("credit");
        Integer preCourseId = dataRequest.getInteger("preCourseId");
        Integer teacherId=dataRequest.getInteger("teacherId");
        Optional<Course> op;
        Course c= null;
        Optional<Teacher> tc=null;
        if(teacherId!=null){
            tc=teacherRepository.findTeacherByTeacherId(teacherId);
        }

        if(courseId != null) {
            op = courseRepository.findById(courseId);
            if(op.isPresent())
                c= op.get();
        }
        if(c== null)
            c = new Course();
        Course pc =null;
        if(preCourseId != null) {
            op = courseRepository.findById(preCourseId);
            if(op.isPresent())
                pc = op.get();
        }
        c.setNum(num);
        c.setName(name);
        c.setCredit(credit);
        c.setCoursePath(coursePath);
        c.setPreCourse(pc);
        c.setTeacher(tc.get());
        courseRepository.save(c);
        return CommonMethod.getReturnMessageOK();
    }
    @PostMapping("/courseDelete")
    public DataResponse courseDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        Optional<Course> op;
        Course c= null;
        if(courseId != null) {
            op = courseRepository.findById(courseId);
            if(op.isPresent()) {
                c = op.get();
                courseRepository.delete(c);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }
    @PostMapping("/studentCourseSave")
    public DataResponse studentCourseSave(@Valid @RequestBody DataRequest dataRequest){
        Integer courseId=dataRequest.getInteger("courseId");
        Integer userId=dataRequest.getInteger("userId");
        Optional<Course> course=courseRepository.findCourseByCourseId(courseId);
        Optional<User> user=null;
        user=userRepository.findByUserId(userId);
        String studentNum=user.get().getPerson().getNum();
        Optional<Student> student = studentRepository.findByPersonNum(studentNum);
        Score score=new Score();
        score.setStudent(student.get());
        score.setCourse(course.get());
        if (scoreRepository.findByStudentStudentIdAndCourseCourseId(student.get().getStudentId(),courseId).isPresent())
        {return CommonMethod.getReturnMessageError("已选该课程");}
        scoreRepository.saveAndFlush(score);
        return CommonMethod.getReturnMessageOK();
    }
    @PostMapping("/getStudentCourseList")
    public DataResponse getStudentCourseList(@Valid @RequestBody DataRequest dataRequest){
        Integer userId =dataRequest.getInteger("userId");
        String numName=dataRequest.getString("numName");
        Optional<User> user =null;
        user =userRepository.findByUserId(userId);
        Optional<Student> student =studentRepository.findByPersonNum(user.get().getPerson().getNum());
        List<Score> studentCourse=scoreRepository.findCourseListByStudentIdAndNumName(student.get().getStudentId(),numName);
        List dataList = new ArrayList();
        Course pc;
        Teacher tc;
        Map m;
        for (Score c : studentCourse) {
            m = new HashMap();
            m.put("courseId", c.getCourse().getCourseId()+"");
            m.put("num",c.getCourse().getNum());
            m.put("name",c.getCourse().getName());
            m.put("credit",c.getCourse().getCredit());
            m.put("coursePath",c.getCourse().getCoursePath());
            pc =c.getCourse().getPreCourse();
            if(pc != null) {
                m.put("preCourse",pc.getName());
                m.put("preCourseId",pc.getCourseId());
            }
            tc=c.getCourse().getTeacher();
            if (tc!=null){
                m.put("teacherName",tc.getPerson().getName());
                m.put("teacherNum",tc.getPerson().getNum());
                m.put("teacherId",tc.getTeacherId());
            }
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }
    @PostMapping("/studentCourseDelete")
    public DataResponse studentCourseDelete(@Valid @RequestBody DataRequest dataRequest){
        Integer courseId=dataRequest.getInteger("courseId");
        Integer userId=dataRequest.getInteger("userId");
        Optional<User> user =userRepository.findByUserId(userId);
        Optional<Student> student = studentRepository.findByPersonNum(user.get().getPerson().getNum());
        Optional<Score> score=scoreRepository.findByStudentStudentIdAndCourseCourseId(student.get().getStudentId(),courseId);
        if (score.get().getMark()!=null){
            return CommonMethod.getReturnMessageError("不能退选已有成绩的课程");
        }
        scoreRepository.delete(score.get());
        return CommonMethod.getReturnMessageOK();
    }
    @PostMapping("/getTeacherCourse")
    public DataResponse getTeacherCourse(@Valid @RequestBody DataRequest dataRequest){
        System.out.println(dataRequest.getData());
        String numName = dataRequest.getString("numName");
        Integer userId = dataRequest.getInteger("userId");
        Optional<User> user=userRepository.findByUserId(userId);
        List dataList =new ArrayList();
        List<Course> courses = courseRepository.findCourseListByTeacherNumAndNumName(user.get().getPerson().getNum(),numName);
        Map m;
        Course pc;
        for (Course c:courses){
            m = new HashMap();
            m.put("courseId", c.getCourseId()+"");
            m.put("num",c.getNum());
            m.put("name",c.getName());
            m.put("credit",c.getCredit());
            m.put("coursePath",c.getCoursePath());
            pc =c.getPreCourse();
            if(pc != null) {
                m.put("preCourse",pc.getName());
                m.put("preCourseId",pc.getCourseId());
            }
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }
}
