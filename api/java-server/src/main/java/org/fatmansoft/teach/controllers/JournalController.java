package org.fatmansoft.teach.controllers;

import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.extend.impl.FSDefaultCacheStore;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.Journal;
import org.fatmansoft.teach.models.Leave;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.CourseRepository;
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

public class JournalController {
    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， TeachController 中要使用StudentRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， studentRepository 相当于StudentRepository接口实现对象的一个引用，由框架完成对这个引用的复制，
    // TeachController中的方法可以直接使用
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;
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


    public List getJournalMapList(String numName) {
        List dataList = new ArrayList();
        List<Journal> sList = journalRepository.findAll();  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Journal sc;
        Student s;
        Course c;
        Leave l;
        Map m;
        String leaveParas,consumeParas;
        for(int i = 0; i < sList.size();i++) {
            sc = sList.get(i);
            m = new HashMap();
            m.put("id", sc.getId());
            m.put("leaveRecord",sc.getLeaveRecord());
            m.put("consumeRecord",sc.getConsumeRecord());
            //leaveParas = "model=leave&id=" + l.getId();
            leaveParas = "model=leave&id=" + sc.getId()+"leaveRecord="+sc.getLeaveRecord();
            m.put("leave","请假管理");
            m.put("leaveParas",leaveParas);
            consumeParas = "model=consume&id=" + sc.getId()+"consumeParas="+sc.getConsumeRecord();
            m.put("consume","消费管理");
            m.put("consumeParas",consumeParas);
            dataList.add(m);
        }
        return dataList;
    }
    @PostMapping("/journalInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse journalInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getJournalMapList("");
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    //  学生信息删除方法
    //Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/journalDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse journalDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        Journal s= null;
        Optional<Journal> op;
        if(id != null) {
            op= journalRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            journalRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }


    @PostMapping("/journalEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse journalEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Journal sc= null;
        Student s;
        Course c;
        Optional<Journal> op;
        if(id != null) {
            op= journalRepository.findById(id);
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
        List courseIdList = new ArrayList();
        List<Course> cList = courseRepository.findAll();
        for(i = 0; i <sList.size();i++) {
            c =cList.get(i);
            m = new HashMap();
            m.put("label",c.getCourseName());
            m.put("value",c.getId());
            courseIdList.add(m);
        }
        Map form = new HashMap();
        form.put("studentId","");
        form.put("courseId","");
        if(sc != null) {
            form.put("id",sc.getId());
            form.put("leaveRecord",sc.getLeaveRecord());
            form.put("consumeRecord",sc.getConsumeRecord());

        }
        form.put("studentIdList",studentIdList);
        form.put("courseIdList",courseIdList);
        return CommonMethod.getReturnData(form); //这里回传包含学生信息的Map对象
    }
    public synchronized Integer getNewJournalId(){
        Integer  id = journalRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    @PostMapping("/journalEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse journalEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form,"id");
        String leaveRecord = CommonMethod.getString(form,"leaveRecord");
        String consumeRecord = CommonMethod.getString(form,"consumeRecord");
        Journal sc= null;
        Student s= null;
        Course c = null;
        Optional<Journal> op;
        if(id != null) {
            op= journalRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                sc = op.get();
            }
        }
        if(sc == null) {
            sc = new Journal();   //不存在 创建实体对象
            id = getNewJournalId(); //获取鑫的主键，这个是线程同步问题;
            sc.setId(id);  //设置新的id
        }
        sc.setLeaveRecord(leaveRecord); //设置属性
        sc.setConsumeRecord(consumeRecord);

        journalRepository.save(sc);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(sc.getId());  // 将记录的id返回前端
    }


}