package org.fatmansoft.teach.service;

import org.fatmansoft.teach.models.Person;
import org.fatmansoft.teach.models.Fee;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.util.ComDataUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FeeService {
    public Map getMapFromFee(Fee fee) {
        Map m = new HashMap();
        Student student;
        if(fee == null)
            return m;
        student=fee.getStudent();
        m.put("day",fee.getDay());
        m.put("money",fee.getMoney());
        m.put("feeId", fee.getFeeId());
        m.put("studentId", student.getStudentId());
        m.put("num",student.getPerson().getNum());
        m.put("name",student.getPerson().getName());
        return m;
    }

}

