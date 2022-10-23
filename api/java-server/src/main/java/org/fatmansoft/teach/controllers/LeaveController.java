//package org.fatmansoft.teach.controllers;

//public class LeaveController {
//}
package org.fatmansoft.teach.controllers;

import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.extend.impl.FSDefaultCacheStore;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.fatmansoft.teach.models.Journal;
import org.fatmansoft.teach.models.Leave;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.JournalRepository;
import org.fatmansoft.teach.repository.LeaveRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.service.IntroduceService;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.validation.Valid;
import java.io.InputStream;
import java.util.*;

// origins： 允许可访问的域列表
// maxAge:准备响应前的缓存持续的最大时间（以秒为单位）。
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")

public class LeaveController {
    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， TeachController 中要使用StudentRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， studentRepository 相当于StudentRepository接口实现对象的一个引用，由框架完成对这个引用的复制，
    // TeachController中的方法可以直接使用
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private JournalRepository journalRepository;
    @Autowired
    private LeaveRepository leaveRepository;
    @Autowired
    private IntroduceService introduceService;
    @Autowired
    private ResourceLoader resourceLoader;
    private FSDefaultCacheStore fSDefaultCacheStore = new FSDefaultCacheStore();

    //getStudentMapList 查询所有学号或姓名与numName相匹配的学生信息，并转换成Map的数据格式存放到List
    //
    // Map 对象是存储数据的集合类，框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似，
    //下面方法是生成前端Table数据的示例，List的每一个Map对用显示表中一行的数据
    //Map 每个键值对，对应每一个列的值，如m.put("studentNum",s.getStudentNum())， studentNum这一列显示的是具体的学号的值
    //按照我们测试框架的要求，每个表的主键都是id, 生成表数据是一定要用m.put("id", s.getId());将id传送前端，前端不显示，
    //但在进入编辑页面是作为参数回传到后台.


    public List getLeaveMapList(String numName) {
        List dataList = new ArrayList();
        List<Leave> sList = leaveRepository.findAll();  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Leave sc;
        Student s;
        Journal c;
        Map m;
        String courseParas,studentNameParas;
        for(int i = 0; i < sList.size();i++) {
            sc = sList.get(i);
            s = sc.getStudent();
            c = sc.getJournal();
            m = new HashMap();
            m.put("id", sc.getId());
            m.put("studentNum",s.getStudentNum());
            m.put("studentName",s.getStudentName());
           // m.put("leaveRecord",c.getLeaveRecord());
            m.put("leaveReason",sc.getLeaveReason());
            if("1".equals(sc.getPass())) {    //数据库存的是编码，显示是名称
                m.put("pass","申请已通过");
            }else{
                m.put("pass","申请未通过");
            }
            m.put("detailouttime",sc.getDetailouttime());
            m.put("detailintime",sc.getDetailintime());
            m.put("detailapplytime",sc.getDetailapplytime());
            m.put("outtime", DateTimeTool.parseDateTime(sc.getOuttime(),"yyyy-MM-dd"));  //时间格式转换字符串
            m.put("intime", DateTimeTool.parseDateTime(sc.getIntime(),"yyyy-MM-dd"));  //时间格式转换字符串
            m.put("applytime", DateTimeTool.parseDateTime(sc.getApplytime(),"yyyy-MM-dd"));  //时间格式转换字符串
            dataList.add(m);
        }
        return dataList;
    }
    @PostMapping("/leaveInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse leaveInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getLeaveMapList("");
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    //  学生信息删除方法
    //Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/leaveDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse leaveDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        Leave s= null;
        Optional<Leave> op;
        if(id != null) {
            op= leaveRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            leaveRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }


    @PostMapping("/leaveEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse leaveEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Leave sc= null;
        Student s;
        Optional<Leave> op;
        if(id != null) {
            op= leaveRepository.findById(id);
            if(op.isPresent()) {
                sc = op.get();
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
        Map form = new HashMap();
        form.put("studentId","");
        if(sc != null) {
            form.put("id",sc.getId());
            form.put("studentId",sc.getStudent().getId());
            form.put("leaveReason",sc.getLeaveReason());
            form.put("detailouttime",sc.getDetailouttime());
            form.put("detailintime",sc.getDetailintime());
            form.put("detailapplytime",sc.getDetailapplytime());
            form.put("outtime", DateTimeTool.parseDateTime(sc.getOuttime(),"yyyy-MM-dd"));
            form.put("intime", DateTimeTool.parseDateTime(sc.getIntime(),"yyyy-MM-dd"));
            form.put("applytime", DateTimeTool.parseDateTime(sc.getApplytime(),"yyyy-MM-dd"));
            form.put("pass",sc.getPass());

        }
        form.put("studentIdList",studentIdList);
        return CommonMethod.getReturnData(form); //这里回传包含学生信息的Map对象
    }
    public synchronized Integer getNewLeaveId(){
        Integer  id = leaveRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    @PostMapping("/leaveEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse leaveEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form,"id");
        Integer studentId = CommonMethod.getInteger(form,"studentId");
        String leaveReason = CommonMethod.getString(form,"leaveReason");
        String pass = CommonMethod.getString(form,"pass");
        String detailouttime = CommonMethod.getString(form,"detailouttime");
        String detailintime = CommonMethod.getString(form,"detailintime");
        String detailapplytime = CommonMethod.getString(form,"detailapplytime");
        Date outtime = CommonMethod.getDate(form,"outtime");
        Date intime = CommonMethod.getDate(form,"intime");
        Date applytime = CommonMethod.getDate(form,"applytime");
        Leave sc= null;
        Student s= null;
        Optional<Leave> op;
        if(id != null) {
            op= leaveRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                sc = op.get();
            }
        }
        if(sc == null) {
            sc = new Leave();   //不存在 创建实体对象
            id = getNewLeaveId(); //获取鑫的主键，这个是线程同步问题;
            sc.setId(id);  //设置新的id
        }
        sc.setStudent(studentRepository.findById(studentId).get());  //设置属性
        sc.setLeaveReason(leaveReason);
        sc.setOuttime(outtime);
        sc.setIntime(intime);
        sc.setDetailapplytime(detailapplytime);
        sc.setDetailouttime(detailouttime);
        sc.setDetailintime(detailintime);
        sc.setApplytime(applytime);
        sc.setPass(pass);
        leaveRepository.save(sc);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(sc.getId());  // 将记录的id返回前端
    }


}