package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Honor;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.HonorRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")
public class HonorController {
    @Autowired
    private HonorRepository honorRepo;
    @Autowired
    private StudentRepository studentRepository;

    public ArrayList<HashMap<String, Object>> getHonorMapList(String numName) {
        List<Honor> entities = honorRepo.findHonorListByNumName(numName);
        ArrayList<HashMap<String, Object>> mapList = new ArrayList<>();
        Student s;
        for (final Honor e : entities) {
            s = e.getStudent();
            final HashMap<String, Object> m = new HashMap<>();
            m.put("id", e.getId());
            m.put("studentNum",s.getStudentNum());
            m.put("studentName",s.getStudentName());
            m.put("honorName", e.getHonorName());
            m.put("credit", e.getCredit());
            m.put("honorTime", DateTimeTool.parseDateTime(e.getHonorTime(),"yyyy-MM-dd"));  //时间格式转换字符串
            mapList.add(m);
        }
        return mapList;
    }

    @PostMapping("/honorInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse honorInit(@Valid @RequestBody DataRequest req) {
        List<HashMap<String, Object>> mapList = getHonorMapList("");
        return CommonMethod.getReturnData(mapList);
    }

    @PostMapping("/honorQuery")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse honorQuery(@Valid @RequestBody DataRequest req) {
        String numName = req.getString("numName");
        List<HashMap<String, Object>> mapList = getHonorMapList(numName);
        return CommonMethod.getReturnData(mapList);
    }

    @PostMapping("/honorEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse honorEditInit(@Valid @RequestBody DataRequest req) {
        Integer id = req.getInteger("id");
        Student s;
        Honor e = null;
        if (id != null) {
            Optional<Honor> op = honorRepo.findById(id);
            if (op.isPresent()) {
                e = op.get();
            }
        }
        Map m;
        int i;
        List studentIdList = new ArrayList();
        List<Student> sList = studentRepository.findAll();
        for(i = 0; i <sList.size();i++) {
            s =sList.get(i);
            m = new HashMap();
            m.put("label",s.getStudentName());
            m.put("value",s.getId());
            studentIdList.add(m);
        }
        final HashMap<String, Object> form = new HashMap<>();
        form.put("studentId","");
        if (e != null) {
            form.put("id", e.getId());
            form.put("studentId",e.getStudent().getId());
            form.put("honorName", e.getHonorName());
            form.put("credit", e.getCredit());
            form.put("honorTime", DateTimeTool.parseDateTime(e.getHonorTime(),"yyyy-MM-dd"));
        }
        form.put("studentIdList",studentIdList);
        return CommonMethod.getReturnData(form);
    }

    public synchronized Integer getNewHonorId() {
        Integer id = honorRepo.getMaxId();
        return (id == null ? 1 : id + 1);
    }

    @PostMapping("/honorEditSubmit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse honorEditSubmit(@Valid @RequestBody DataRequest req) {
        HashMap<String, Object> form = (HashMap<String, Object>) req.getMap("form");
        Integer id = CommonMethod.getInteger(form, "id");
        Honor e = null;
        if (id != null) {
            Optional<Honor> op = honorRepo.findById(id);
            if (op.isPresent()) {
                e = op.get();
            }
        }
        if (e == null) {
            e = new Honor();
            id = getNewHonorId();
            e.setId(id);
        }
        String honorName = CommonMethod.getString(form, "honorName");
        Integer studentId = CommonMethod.getInteger(form,"studentId");
        Integer credit = CommonMethod.getInteger(form, "credit");
        Date honorTime = CommonMethod.getDate(form,"honorTime");
        e.setStudent(studentRepository.findById(studentId).get());
        e.setHonorTime(honorTime);
        e.setHonorName(honorName);
        e.setCredit(credit);
        honorRepo.save(e);
        return CommonMethod.getReturnData(e.getId());
    }

    @PostMapping("/honorDelete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse honorDelete(@Valid @RequestBody DataRequest req) {
        final Integer id = req.getInteger("id");
        if (id != null) {
            Optional<Honor> op = honorRepo.findById(id);
            if (op.isPresent()) {
                final Honor e = op.get();
                honorRepo.delete(e);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }



}