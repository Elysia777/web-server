package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Activity;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.StudentActivity;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.ActivityRepository;
import org.fatmansoft.teach.repository.PersonRepository;
import org.fatmansoft.teach.repository.StudentActivityRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/studentActivity")
public class StudentActivityController {
    //学生活动报名
    @Autowired
    private StudentActivityRepository studentActivityRepository;  //活动数据操作自动注入
    @Autowired
    private  ActivityRepository activityRepository;
    @Autowired
    private StudentRepository studentRepository;  //学生数据操作自动注入
    @Autowired
    private PersonRepository personRepository;  //人员数据操作自动注入

    public synchronized Integer getNewStudentActivityId(){
        Integer  id = studentActivityRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };

    public Map getMapFromStudentActivity(StudentActivity e) {
        Map m = new HashMap();
        Student s;
        Activity a ;
        if(e == null)
            return m;
        s = e.getStudent();
        a = e.getActivity();
        if(s == null)
            return m;
        if(a == null)
            return m;
        m.put ("num",s.getPerson().getNum());
        m.put ("name",s.getPerson().getName());
        m.put("activityName", a.getActivityName());
        m.put("date", a.getDate());
        m.put("time", a.getTime());
        String type = a.getType();
        m.put("type", type);
        m.put("typeName", ComDataUtil.getInstance().getDictionaryLabelByValue("TBM", type)); //活动类型类型的值转换成数据类型名
        m.put("organizer", a.getOrganizer());
        m.put("enrollFee", a.getEnrollFee());
        m.put("people", a.getPeople());
        m.put("remainPeople", a.getRemainPeople());
        m.put("activityAddress", a.getActivityAddress());
        m.put("studentActivityId", e.getStudentActivityId());
        m.put("activityId", a.getActivityId());
        m.put("studentId", s.getStudentId());
        return m;
    }
    //管理员端查找数据
    public Map getMapFromAdminActivity(StudentActivity e) {
        Map m = new HashMap();
        Student s;
        Activity a ;
        if(e == null)
            return m;
        s = e.getStudent();
        a = e.getActivity();
        if(s == null)
            return m;
        if(a == null)
            return m;
        m.put ("num",s.getPerson().getNum());
        m.put("name",s.getPerson().getName());
        m.put("activityName", a.getActivityName());
        m.put("date", a.getDate());
        m.put("time", a.getTime());
        String type = a.getType();
        m.put("type", type);
        m.put("typeName", ComDataUtil.getInstance().getDictionaryLabelByValue("TBM", type)); //活动类型类型的值转换成数据类型名
        m.put("organizer", a.getOrganizer());
        m.put("enrollFee", a.getEnrollFee());
        m.put("people", a.getPeople());
        m.put("remainPeople", a.getRemainPeople());
        m.put("activityAddress", a.getActivityAddress());
        m.put("studentActivityId", e.getStudentActivityId());
        m.put("activityId", a.getActivityId());
        m.put("studentId", s.getStudentId());
        return m;
    }
    public List getStudentActivityMapList(String activityName) {
        List dataList = new ArrayList();
        List<StudentActivity> eList = studentActivityRepository.findStudentActivityListByActivityName(activityName);  //数据库查询操作
        if(eList == null || eList.size() == 0)
            return dataList;
        for(int i = 0; i < eList.size();i++) {
            dataList.add(getMapFromStudentActivity(eList.get(i)));
        }
        return dataList;
    }
    public List getStudentActivity1MapList(String num) {
        List dataList = new ArrayList();
        List<StudentActivity> eList = studentActivityRepository.findByStudentNum(num);  //数据库查询操作
        if(eList == null || eList.size() == 0)
            return dataList;
        for(int i = 0; i < eList.size();i++) {
            dataList.add(getMapFromStudentActivity(eList.get(i)));
        }
        return dataList;
    }
    public List getAdminActivityMapList(String activityName) {
        List dataList = new ArrayList();
        List<StudentActivity> eList = studentActivityRepository.findStudentActivityListByActivityName(activityName);  //数据库查询操作
        if(eList == null || eList.size() == 0)
            return dataList;
        for(int i = 0; i < eList.size();i++) {
            dataList.add(getMapFromAdminActivity(eList.get(i)));
        }
        return dataList;
    }
    public List getAdminActivity1MapList(String num) {
        List dataList = new ArrayList();
        List<StudentActivity> eList = studentActivityRepository.findByStudentNum(num);  //数据库查询操作
        if(eList == null || eList.size() == 0)
            return dataList;
        for(int i = 0; i < eList.size();i++) {
            dataList.add(getMapFromAdminActivity(eList.get(i)));
        }
        return dataList;
    }
    /**
     *  getStudentActivityList 学生活动管理 点击查询按钮请求
     *  前台请求参数 numName 学号或名称的 查询串
     * 返回前端 存储学生信息的 MapList 框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似
     * @return
     */
    @PostMapping("/getStudentActivityItemOptionList")
    public OptionItemList getActivityItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        List<StudentActivity> aList = studentActivityRepository.findStudentActivityListByActivityName("");  //数据库查询操作
        OptionItem item;
        List<OptionItem> itemList = new ArrayList();
        for (StudentActivity a : aList) {
            itemList.add(new OptionItem(a.getStudentActivityId(), a.getActivity().getActivityName(), a.getActivity().getActivityName()));
        }
        return new OptionItemList(0, itemList);
    }
    @PostMapping("/getAdminActivityItemOptionList")
    public OptionItemList getAdminActivityItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        List<StudentActivity> sList = studentActivityRepository.findByStudentNum("");  //数据库查询操作
        OptionItem item;
        List<OptionItem> itemList = new ArrayList();
        for (StudentActivity e : sList) {
            itemList.add(new OptionItem(e.getStudentActivityId(), e.getStudent().getPerson().getNum(), e.getStudent().getPerson().getNum()));
        }
        return new OptionItemList(0, itemList);
    }
    @PostMapping("/getStudentActivityList")
    public DataResponse getStudentActivityList(@Valid @RequestBody DataRequest dataRequest) {
        String activityName= dataRequest.getString("activityName");
        List dataList = getStudentActivityMapList(activityName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/getStudentActivity1List")
    public DataResponse getStudentActivity1List(@Valid @RequestBody DataRequest dataRequest) {
        String num= dataRequest.getString("num");
        List dataList = getStudentActivity1MapList(num);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/getAdminActivityList")
    public DataResponse getAdminActivityList(@Valid @RequestBody DataRequest dataRequest) {
        String activityName= dataRequest.getString("activityName");
        List dataList = getAdminActivityMapList(activityName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/getAdminActivity1List")
    public DataResponse getAdminActivity1List(@Valid @RequestBody DataRequest dataRequest) {
        String num= dataRequest.getString("num");
        List dataList = getAdminActivity1MapList(num);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    /**
     * getStudentInfo 前端点击学生列表时前端获取学生详细信息请求服务
     * @param dataRequest 从前端获取 studentId 查询学生信息的主键 student_id
     * @return  根据studentId从数据库中查出数据，存在Map对象里，并返回前端
     */
    @PostMapping("/getStudentActivityInfo")
    public DataResponse getStudentActivityInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentActivityId = dataRequest.getInteger("studentActivityId");
        StudentActivity a= null;
        Optional<StudentActivity> op;
        if(studentActivityId != null) {
            op= studentActivityRepository.findById(studentActivityId); //根据学生主键从数据库查询学生的信息
            if(op.isPresent()) {
                a = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromStudentActivity(a)); //这里回传包含学生信息的Map对象
    }

    @PostMapping("/studentActivityDelete")
    public DataResponse studentActivityDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentActivityId = dataRequest.getInteger("studentActivityId");  //获取studentActivity_id值
        StudentActivity a = null;
        Optional<StudentActivity> op;
        if (studentActivityId != null) {
            op = studentActivityRepository.findById(studentActivityId);   //查询获得实体对象
            if (op.isPresent()) {
                a = op.get();
            }
        }
        if (a != null) {
            String remainPeople = a.getActivity().getRemainPeople();
            Integer remainPeople1 = Integer.parseInt(remainPeople);
            remainPeople1++;
            remainPeople = Integer.toString(remainPeople1);
            a.getActivity().setRemainPeople(remainPeople);
            studentActivityRepository.delete(a);
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }
    /**
     * studentActivityEditSave 前端学生信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空， new StudentActivity,剩余名额减少,计算新的id， 复制相关属性，保存
     * @return  新建修改学生的主键 student_id 返回前端
     */
    @PostMapping("/studentActivityEditSave")
    public DataResponse studentActivityEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentActivityId = dataRequest.getInteger("studentActivityId");
        Integer activityId = dataRequest.getInteger("activityId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String activityName = CommonMethod.getString(form,"activityName");
        String num = CommonMethod.getString(form,"num");
        Activity a;
        Student s;
        List<StudentActivity> aList = studentActivityRepository.findStudentActivityListByActivityName(activityName);
        a = activityRepository.findActivityByActivityName(CommonMethod.getString(form, "activityName")).get();
        s = studentRepository.findByPersonNum(CommonMethod.getString(form, "num")).get();
        String remainPeople = CommonMethod.getString(form,"remainPeople");
        StudentActivity f = null;
        f = new StudentActivity();   // 创建实体对象
        f.setStudentActivityId(getNewStudentActivityId());//获取新的studentActivity主键
        f.setActivity(a);
        f.setStudent(s);
        for(int i=0;i< aList.size();i++){
            if(aList.get(i).getStudent().getStudentId()==f.getStudent().getStudentId()){
               // return CommonMethod.getReturnMessageError("该活动已报名,不可重复报名！");
                return CommonMethod.getReturnData(1);//重复报名
            }
        }
        Integer remainPeople1 = Integer.parseInt(remainPeople);
        //剩余名额等于0,不能报名
        if(remainPeople1<=0){
            //return CommonMethod.getReturnMessageError("该活动名额已满,报名失败！");
            return CommonMethod.getReturnData(2);//名额已满
        }
        remainPeople1--;
        remainPeople = Integer.toString(remainPeople1);
        a.setRemainPeople(remainPeople);
        f.getActivity().setActivityName((String) form.get("activityName"));
        f.getActivity().setType((String) form.get("type"));
        f.getActivity().setDate((String) form.get("date"));
        f.getActivity().setTime((String) form.get("time"));
        f.getActivity().setOrganizer((String) form.get("organizer"));
        f.getActivity().setEnrollFee((String) form.get("enrollFee"));
        f.getActivity().setActivityAddress((String) form.get("activityAddress"));
        f.getStudent().getPerson().setNum((String) form.get("num"));
        studentActivityRepository.save(f);  // 修改保存学生活动信息
        //return CommonMethod.getReturnData(f.getStudentActivityId());  // 将studentActivityId返回前端
        return CommonMethod.getReturnData(3);//成功报名
    }

}
