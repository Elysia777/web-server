package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Activity;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.payload.response.OptionItem;
import org.fatmansoft.teach.payload.response.OptionItemList;
import org.fatmansoft.teach.repository.ActivityRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    //总的报名信息
    @Autowired
    private ActivityRepository activityRepository;  //活动数据操作自动注入
    public synchronized Integer getNewActivityId() {
        Integer id = activityRepository.getMaxId();  // 查询最大的id
        if (id == null)
            id = 1;
        else
            id = id + 1;
        return id;
    };

    /**
     * getMapFromActivity将学生表属性数据转换复制MAp集合里
     *
     * @param
     * @return
     */
    public Map getMapFromActivity(Activity a) {
        Map m = new HashMap();
        if (a == null)
            return m;
        m.put("activityId", a.getActivityId());
        m.put("activityName", a.getActivityName());
        m.put("date", a.getDate());
        m.put("time", a.getTime());
        String type = a.getType();
        m.put("type", type);
   //     m.put("typeName", ComDataUtil.getInstance().getDictionaryLabelByValue("TBM", type)); //活动类型的值转换成数据类型名
        m.put("organizer", a.getOrganizer());
        m.put("enrollFee", a.getEnrollFee());
        m.put("people", a.getPeople());
        m.put("remainPeople", a.getRemainPeople());
        m.put("activityAddress", a.getActivityAddress());
//        m.put("state",a.getState());
        return m;
    }


    /**
     * getActivityMapList 根据输入参数查询得到学生数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生
     * @param activityName 输入参数
     * @return Map List 集合
     */
    public List getActivityMapList(String activityName) {
        List dataList = new ArrayList();
        List<Activity> aList = activityRepository.findActivityListByActivityName(activityName);  //数据库查询操作
        if (aList == null || aList.size() == 0)
            return dataList;
        for (int i = 0; i < aList.size(); i++) {
            dataList.add(getMapFromActivity(aList.get(i)));
        }
        return dataList;
    }
    public List getActivity1MapList(String num) {
        List dataList = new ArrayList();
        List<Activity> aList = activityRepository.findActivityListByActivityName(num);  //数据库查询操作
        if (aList == null || aList.size() == 0)
            return dataList;
        for (int i = 0; i < aList.size(); i++) {
            dataList.add(getMapFromActivity(aList.get(i)));
        }
        return dataList;
    }



    /**
     * getActivityList 活动管理 点击查询按钮请求
     * 前台请求参数 activityName 活动名称 查询串
     * 返回前端 存储活动信息的 MapList 框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似
     *
     * @return
     */
    @PostMapping("/getActivityItemOptionList")
    public OptionItemList getActivityItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        List<Activity> aList = activityRepository.findActivityListByActivityName("");  //数据库查询操作
        OptionItem item;
        List<OptionItem> itemList = new ArrayList();
        for (Activity a : aList) {
            itemList.add(new OptionItem(a.getActivityId(), a.getActivityName(), a.getActivityName()));
        }
        return new OptionItemList(0, itemList);
    }


    @PostMapping("/getActivityList")
    public DataResponse getActivityList(@Valid @RequestBody DataRequest dataRequest) {
        String activityName = dataRequest.getString("activityName");
        List dataList = getActivityMapList(activityName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    @PostMapping("/getActivity1List")
    public DataResponse getActivity1List(@Valid @RequestBody DataRequest dataRequest) {
        String num = dataRequest.getString("num");
        List dataList = getActivity1MapList(num);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    /**
     * getActivityInfo 前端点击活动列表时前端获取日常活动详细信息请求服务
     *
     * @param dataRequest 从前端获取 activityId 查询学生信息的主键 activity_id
     * @return 根据studentId从数据库中查出数据，存在Map对象里，并返回前端
     */
    @PostMapping("/getActivityInfo")
    public DataResponse getActivityInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer activityId = dataRequest.getInteger("activityId");
        Activity a = null;
        Optional<Activity> op;
        if (activityId != null) {
            op = activityRepository.findById(activityId); //根据学生主键从数据库查询学生的信息
            if (op.isPresent()) {
                a = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromActivity(a)); //这里回传包含学生信息的Map对象
    }
    /**
     * activityDelete 删除活动信息Web服务 Activity页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
     * 与activity相关联studentActivity的表信息都会被删除
     * @param dataRequest  前端activityId 药删除的学生的主键 activity_id
     * @return  正常操作
     */
    @PostMapping("/activityDelete")
    public DataResponse activityDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer activityId = dataRequest.getInteger("activityId");  //获取activity_id值
        Activity a = null;
        Optional<Activity> op;
        if (activityId != null) {
            op = activityRepository.findById(activityId);   //查询获得实体对象
            if (op.isPresent()) {
                a = op.get();
            }
        }
        if (a != null) {
            activityRepository.delete(a);
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }
    /**
     * activityEditSave 前端学生信息提交服务
     * 前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
     * 实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空， new Activity 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
     * activityId不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
     * @return  新建修改学生的主键 activity_id 返回前端
     */
    @PostMapping("/activityEditSave")
    public DataResponse activityEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer activityId = dataRequest.getInteger("activityId");
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        String activityName = CommonMethod.getString(form, "activityName");  //Map 获取属性的值
        Activity f = null;
        Optional<Activity> op;
        List<Activity> sList = activityRepository.findActivityListByActivityName(activityName);
        if (activityId != null) {
            op = activityRepository.findById(activityId);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                f = op.get();//获得family
            }
        }

        if (f == null) {
            f = new Activity();   // 创建实体对象
            f.setActivityId(getNewActivityId());//获取新的family主键
        }
//        f.setState("未报名");
        f.setActivityName((String) form.get("activityName"));
        f.setType((String) form.get("type"));
        f.setDate((String) form.get("date"));
        f.setTime((String) form.get("time"));
        f.setOrganizer((String) form.get("organizer"));
        f.setEnrollFee((String) form.get("enrollFee"));
        f.setPeople((String) form.get("people"));
        f.setRemainPeople((String) form.get("remainPeople"));
        f.setActivityAddress((String) form.get("activityAddress"));
        activityRepository.save(f);  // 修改活动课程信息
        return CommonMethod.getReturnData(f.getActivityId());  // 将activityId返回前端
    }


}
