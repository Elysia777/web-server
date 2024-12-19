package org.fatmansoft.teach.controllers;
import org.fatmansoft.teach.models.Fee;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;
import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.*;
import org.fatmansoft.teach.service.BaseService;
import org.fatmansoft.teach.service.FeeService;
import org.fatmansoft.teach.service.SystemService;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.List;
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/fee")
public class FeeController {

    @Autowired
    private PersonRepository personRepository;  //人员数据操作自动注入
    @Autowired
    private FeeRepository feeRepository;  //学生数据操作自动注入
    @Autowired
    private UserRepository userRepository;  //学生数据操作自动注入
    @Autowired
    private UserTypeRepository userTypeRepository; //用户类型数据操作自动注入
    @Autowired
    private PasswordEncoder encoder;  //密码服务自动注入
    @Autowired
    private BaseService baseService;   //基本数据处理数据操作自动注入
    @Autowired
    private FeeService feeService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private StudentRepository studentRepository;

    /**
     * getMapFromPerson 将学生表属性数据转换复制MAp集合里
     * @param
     * @return
     */

    /**
     * getPersonMapList 根据输入参数查询得到学生数据的 Map List集合 参数为空 查出说有学生， 参数不为空，查出人员编号或人员名称 包含输入字符串的学生
     *
     * @param numName 输入参数
     * @return Map List 集合
     */
    public List getfeeMapList(String numName) {
        List dataList = new ArrayList();
        List<Fee> sList = feeRepository.findFeeListByStudentNumName(numName);  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        for (int i = 0; i < sList.size(); i++) {
            dataList.add(feeService.getMapFromFee(sList.get(i)));
        }
        return dataList;
    }

    @PostMapping("/getFeeList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getfeeList(@Valid @RequestBody DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        if (numName==null){
            numName="";
        }
        List dataList = getfeeMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }


    @PostMapping("/feeDelete")
    public DataResponse feeDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer feeId = dataRequest.getInteger("feeId");
        Fee s = null;
        Optional<Fee> op;
        if (feeId != null) {
            op = feeRepository.findById(feeId);   //查询获得实体对象
            if (op.isPresent()) {
                s = op.get();
            }
        }
        if (s != null) {
            feeRepository.delete(s);    //首先数据库永久删除学生信息
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    @PostMapping("/getFeeInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getfeeInfo(@Valid @RequestBody DataRequest dataRequest) {
        Integer feeId = dataRequest.getInteger("feeId");
        Fee s = null;
        Optional<Fee> op;
        if (feeId != null) {
            op = feeRepository.findById(feeId); //根据学生主键从数据库查询学生的信息
            if (op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(feeService.getMapFromFee(s)); //这里回传包含学生信息的Map对象
    }

    @PostMapping("/feeEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse feeEditSave(@Valid @RequestBody DataRequest dataRequest) {
        Integer feeId = dataRequest.getInteger("feeId");
        Integer studentId=dataRequest.getInteger("studentId");
        String num= dataRequest.getString("num");
        String money=dataRequest.getString("money");
        String day=dataRequest.getString("day");
        Fee fee = null;
        Student student;
        Optional<Fee> op;
        if (feeId != null) {
            op = feeRepository.findById(feeId);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                fee = op.get();
            }
        }
        Optional<Student> optionalStudent=studentRepository.findByPersonNum(num) ;//查询是否存在num的人员
        if(!optionalStudent.isPresent()){
            return CommonMethod.getReturnMessageError("该人员不存在");
        }
        student=optionalStudent.get();
        if (fee == null) {
            fee = new Fee();   // 创建实体对象
            fee.setStudent(student);
        }
       fee.setDay(day);
        fee.setMoney(Double.parseDouble(money));
        fee.setDay(day);
        feeRepository.save(fee);  //修改保存费用信息
        return CommonMethod.getReturnMessageOK();  // 将feeId返回前端
    }
}
