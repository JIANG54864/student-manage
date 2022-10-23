package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.service.IntroduceService;
import org.fatmansoft.teach.util.CommonMethod;
import org.fatmansoft.teach.util.DateTimeTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import org.fatmansoft.teach.models.Homework;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.repository.HomeworkRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.models.Selection;
import org.fatmansoft.teach.repository.SelectionRepository;
import org.fatmansoft.teach.models.Attendance;
import org.fatmansoft.teach.repository.AttendanceRepository;

// origins： 允许可访问的域列表
// maxAge:准备响应前的缓存持续的最大时间（以秒为单位）。
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")

public class CourseController {
    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， TeachController 中要使用StudentRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， studentRepository 相当于StudentRepository接口实现对象的一个引用，由框架完成对这个引用的复制，
    // TeachController中的方法可以直接使用
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private IntroduceService introduceService;
    @Autowired
    private HomeworkRepository homeworkRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private SelectionRepository selectionRepository;
    //getStudentMapList 查询所有学号或姓名与numName相匹配的学生信息，并转换成Map的数据格式存放到List
    //
    // Map 对象是存储数据的集合类，框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似，
    //下面方法是生成前端Table数据的示例，List的每一个Map对用显示表中一行的数据
    //Map 每个键值对，对应每一个列的值，如m.put("studentNum",s.getStudentNum())， studentNum这一列显示的是具体的学号的值
    //按照我们测试框架的要求，每个表的主键都是id, 生成表数据是一定要用m.put("id", s.getId());将id传送前端，前端不显示，
    //但在进入编辑页面是作为参数回传到后台.
    public List getCourseMapList(String numName) {
        List dataList = new ArrayList();
        List<Course> sList = courseRepository.findCourseListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Course s;
        Map m;
        String homeworkParas,selectionParas,attendanceParas,courseNameParas;
        for(int i = 0; i < sList.size();i++) {
            s = sList.get(i);
            m = new HashMap();
            m.put("id", s.getId());
            m.put("courseNum",s.getCourseNum());
            homeworkParas = "model=homework&courseId=" + s.getId()+"courseName="+s.getCourseName();
            selectionParas ="model=selection&courseId=" + s.getId()+"courseName="+s.getCourseName();
            attendanceParas ="model=attendance&courseId=" + s.getId()+"courseName="+s.getCourseName();
            courseNameParas ="model=courseEdit&courseName=" + s.getCourseName();
            m.put("courseName",s.getCourseName());
            m.put("courseNameParas",courseNameParas);
            m.put("courseName",s.getCourseName());
            m.put("credit",s.getCredit());
            m.put("preCourse",s.getPreCourse());
            m.put("textbookName",s.getTextbookName());
            m.put("textbookVersion",s.getTextbookVersion());
            m.put("material",s.getMaterial());
            m.put("courseware",s.getCourseware());
            m.put("homework","作业管理");
            m.put("homeworkParas",homeworkParas);
            m.put("selection","选课管理");
            m.put("selectionParas",selectionParas);
            m.put("attendance","考勤管理");
            m.put("attendanceParas",attendanceParas);
            dataList.add(m);
        }
        return dataList;
    }
    //student页面初始化方法
    //Table界面初始是请求列表的数据，这里缺省查出所有学生的信息，传递字符“”给方法getStudentMapList，返回所有学生数据，
    @PostMapping("/courseInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getCourseMapList("");
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    //student页面点击查询按钮请求
    //Table请界面初始是请求列表的数据，从求对象里获得前端界面输入的字符串，作为参数传递给方法getStudentMapList，返回所有学生数据，
    @PostMapping("/courseQuery")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName= dataRequest.getString("numName");
        List dataList = getCourseMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    //studentEdit初始化方法
    //studentEdit编辑页面进入时首先请求的一个方法， 如果是Edit,再前台会把对应要编辑的那个学生信息的id作为参数回传给后端，我们通过Integer id = dataRequest.getInteger("id")
    //获得对应学生的id， 根据id从数据库中查出数据，存在Map对象里，并返回前端，如果是添加， 则前端没有id传回，Map 对象数据为空（界面上的数据也为空白）

    @PostMapping("/courseEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse courseEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Course s= null;
        Optional<Course> op;
        if(id != null) {
            op= courseRepository.findById(id);
            if(op.isPresent()) {
                s = op.get();
            }
        }
        Map form = new HashMap();
        if(s != null) {
            form.put("id",s.getId());
            form.put("courseNum",s.getCourseNum());
            form.put("courseName",s.getCourseName());
            form.put("preCourse",s.getPreCourse());  //这里不需要转换
            form.put("credit",s.getCredit());
            form.put("textbookName",s.getTextbookName());
            form.put("textbookVersion",s.getTextbookVersion());
            form.put("material",s.getMaterial());
            form.put("courseware",s.getCourseware());
        }
        return CommonMethod.getReturnData(form); //这里回传包含学生信息的Map对象
    }
    //  学生信息提交按钮方法
    //相应提交请求的方法，前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
    //实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new Student 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
    //id 不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
    public synchronized Integer getNewCourseId(){
        Integer
                id = courseRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    @PostMapping("/courseEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse courseEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form,"id");
        String courseNum = CommonMethod.getString(form,"courseNum");  //Map 获取属性的值
        String courseName = CommonMethod.getString(form,"courseName");
        String preCourse = CommonMethod.getString(form,"preCourse");
        Integer credit = CommonMethod.getInteger(form,"credit");
        String textbookName= CommonMethod.getString(form,"textbookName");
        String textbookVersion = CommonMethod.getString(form,"textbookVersion");
        String material = CommonMethod.getString(form,"material");
        String courseware = CommonMethod.getString(form,"courseware");
        Course s= null;
        Optional<Course> op;
        if(id != null) {
            op= courseRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s == null) {
            s = new Course();   //不存在 创建实体对象
            id = getNewCourseId(); //获取鑫的主键，这个是线程同步问题;
            s.setId(id);  //设置新的id
        }
        s.setCourseNum(courseNum);  //设置属性
        s.setCourseName(courseName);
        s.setPreCourse(preCourse);
        s.setCredit(credit);
        s.setTextbookVersion(textbookVersion);
        s.setTextbookName(textbookName);
        s.setMaterial(material);
        s.setCourseware(courseware);
        courseRepository.save(s);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(s.getId());  // 将记录的id返回前端
    }

    //  学生信息删除方法
    //Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/courseDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse courseDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        Course s= null;
        Optional<Course> op;
        if(id != null) {
            op= courseRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            courseRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }


    public List getHomeworkMapList(String numName) {
        List dataList = new ArrayList();
        List<Homework> sList = homeworkRepository.findAll();  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Homework sc;
        Student s;
        Course c;
        Map m;
        String courseParas,studentNameParas;
        for(int i = 0; i < sList.size();i++) {
            sc = sList.get(i);
            s = sc.getStudent();
            c = sc.getCourse();
            m = new HashMap();
            m.put("id", sc.getId());
            m.put("studentNum",s.getStudentNum());
            m.put("studentName",s.getStudentName());
            m.put("courseNum",c.getCourseNum());
            m.put("courseName",c.getCourseName());
            if("1".equals(sc.getFsituation())) {    //数据库存的是编码，显示是名称
                m.put("fsituation","作业已完成");
            }else{
                m.put("fsituation","作业未完成");
            }

            m.put("teacher",sc.getTeacher());
            m.put("homeworkNum",sc.getHomeworkNum());
            if("1".equals(sc.getHomeworkLevel())) {    //数据库存的是编码，显示是名称
                m.put("homeworkLevel","优秀");
            }
            if ("2".equals(sc.getHomeworkLevel())){
                m.put("homeworkLevel","良好");
            }
            if("3".equals(sc.getHomeworkLevel())) {
                m.put("homeworkLevel","及格");
            }
            if ("4".equals(sc.getHomeworkLevel())){
                m.put("homeworkLevel", "不及格");
            }
            m.put("subtime", DateTimeTool.parseDateTime(sc.getSubtime(),"yyyy-MM-dd"));  //时间格式转换字符串
            m.put("content",sc.getContent());
            dataList.add(m);
        }
        return dataList;
    }
    @PostMapping("/homeworkInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse homeworkInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getHomeworkMapList("");
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    //  学生信息删除方法
    //Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/homeworkDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse homeworkDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        Homework s= null;
        Optional<Homework> op;
        if(id != null) {
            op= homeworkRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            homeworkRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }


    @PostMapping("/homeworkEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse homeworkEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Homework sc= null;
        Student s;
        Course c;
        Optional<Homework> op;
        if(id != null) {
            op= homeworkRepository.findById(id);
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
        for(i = 0; i <cList.size();i++) {
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
            form.put("studentId",sc.getStudent().getId());
            form.put("courseId",sc.getCourse().getId());
            form.put("teacher",sc.getTeacher());
            form.put("subtime", DateTimeTool.parseDateTime(sc.getSubtime(),"yyyy-MM-dd"));
            form.put("content",sc.getContent());
            form.put("homeworkNum",sc.getHomeworkNum());
            form.put("homeworkLevel",sc.getHomeworkLevel());
            form.put("fsituation",sc.getFsituation());

        }
        form.put("studentIdList",studentIdList);
        form.put("courseIdList",courseIdList);
        return CommonMethod.getReturnData(form); //这里回传包含学生信息的Map对象
    }
    public synchronized Integer getNewHomeworkId(){
        Integer  id = homeworkRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    @PostMapping("/homeworkEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse homeworkEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form,"id");
        Integer studentId = CommonMethod.getInteger(form,"studentId");
        Integer courseId = CommonMethod.getInteger(form,"courseId");
        String teacher = CommonMethod.getString(form,"teacher");
        String content = CommonMethod.getString(form,"content");
        String homeworkNum = CommonMethod.getString(form,"homeworkNum");
        String fsituation = CommonMethod.getString(form,"fsituation");
        Date subtime = CommonMethod.getDate(form,"subtime");
        String homeworkLevel = CommonMethod.getString(form,"homeworkLevel");
        Homework sc= null;
        Student s= null;
        Course c = null;
        Optional<Homework> op;
        if(id != null) {
            op= homeworkRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                sc = op.get();
            }
        }
        if(sc == null) {
            sc = new Homework();   //不存在 创建实体对象
            id = getNewHomeworkId(); //获取鑫的主键，这个是线程同步问题;
            sc.setId(id);  //设置新的id
        }
        sc.setStudent(studentRepository.findById(studentId).get());  //设置属性
        sc.setCourse(courseRepository.findById(courseId).get());
        sc.setTeacher(teacher);
        sc.setSubtime(subtime);
        sc.setContent(content);
        sc.setHomeworkNum(homeworkNum);
        sc.setFsituation(fsituation);
        sc.setHomeworkLevel(homeworkLevel);
        homeworkRepository.save(sc);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(sc.getId());  // 将记录的id返回前端
    }
    public List getSelectionMapList(String numName) {
        List dataList = new ArrayList();
        List<Selection> sList = selectionRepository.findAll();  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Selection sc;
        Student s;
        Course c;
        Map m;
        String courseNameParas;
        for(int i = 0; i < sList.size();i++) {
            sc = sList.get(i);
            s = sc.getStudent();
            c = sc.getCourse();
            m = new HashMap();
            m.put("id", sc.getId());
            m.put("studentNum",s.getStudentNum());
            m.put("studentName",s.getStudentName());
            m.put("courseNum",c.getCourseNum());
            m.put("courseName",c.getCourseName());
            if("1".equals(sc.getAttribute())) {    //数据库存的是编码，显示是名称
                m.put("attribute","必修");
            }else if ("2".equals(sc.getAttribute())){
                m.put("attribute","选修");
            }else{
                m.put("attribute","限选");
            }
            m.put("weeknum",sc.getWeeknum());
            m.put("week",sc.getWeek());
            m.put("stime", DateTimeTool.parseDateTime(sc.getStime(),"yyyy-MM-dd"));  //时间格式转换字符串
            m.put("etime", DateTimeTool.parseDateTime(sc.getEtime(),"yyyy-MM-dd"));  //时间格式转换字符串
            courseNameParas = "model=courseEdit&id=" + c.getId();
            m.put("courseName",sc.getCourse().getCourseName());
            m.put("courseNameParas",courseNameParas);
            dataList.add(m);
        }
        return dataList;
    }
    @PostMapping("/selectionInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse selectionInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getSelectionMapList("");
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    //  学生信息删除方法
    //Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/selectionDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse selectionDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        Selection s= null;
        Optional<Selection> op;
        if(id != null) {
            op= selectionRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            selectionRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }


    @PostMapping("/selectionEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse selectionEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Selection sc= null;
        Student s;
        Course c;
        Optional<Selection> op;
        if(id != null) {
            op= selectionRepository.findById(id);
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
        for(i = 0; i <cList.size();i++) {
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
            form.put("studentId",sc.getStudent().getId());
            form.put("courseId",sc.getCourse().getId());
            form.put("attribute",sc.getAttribute());
            form.put("stime", DateTimeTool.parseDateTime(sc.getStime(),"yyyy-MM-dd"));
            form.put("etime", DateTimeTool.parseDateTime(sc.getEtime(),"yyyy-MM-dd"));
            form.put("weeknum",sc.getWeeknum());
            form.put("week",sc.getWeek());

        }
        form.put("studentIdList",studentIdList);
        form.put("courseIdList",courseIdList);
        return CommonMethod.getReturnData(form); //这里回传包含学生信息的Map对象
    }
    public synchronized Integer getNewSelectionId(){
        Integer  id = selectionRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    @PostMapping("/selectionEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse selectionEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form,"id");
        Integer studentId = CommonMethod.getInteger(form,"studentId");
        Integer courseId = CommonMethod.getInteger(form,"courseId");
        String attribute = CommonMethod.getString(form,"attribute");
        String weeknum = CommonMethod.getString(form,"weeknum");
        String week = CommonMethod.getString(form,"week");
        Date stime = CommonMethod.getDate(form,"stime");
        Date etime = CommonMethod.getDate(form,"etime");
        Selection sc= null;
        Student s= null;
        Course c = null;
        Optional<Selection> op;
        if(id != null) {
            op= selectionRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                sc = op.get();
            }
        }
        if(sc == null) {
            sc = new Selection();   //不存在 创建实体对象
            id = getNewSelectionId(); //获取鑫的主键，这个是线程同步问题;
            sc.setId(id);  //设置新的id
        }
        sc.setStudent(studentRepository.findById(studentId).get());  //设置属性
        sc.setCourse(courseRepository.findById(courseId).get());
        sc.setAttribute(attribute);
        sc.setStime(stime);
        sc.setEtime(etime);
        sc.setWeeknum(weeknum);
        sc.setWeek(week);
        selectionRepository.save(sc);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(sc.getId());  // 将记录的id返回前端
    }

    public List getAttendanceMapList(String numName) {
        List dataList = new ArrayList();
        List<Attendance> sList = attendanceRepository.findAll();  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Attendance sc;
        Student s;
        Course c;
        Map m;
        String courseParas,studentNameParas;
        for(int i = 0; i < sList.size();i++) {
            sc = sList.get(i);
            s = sc.getStudent();
            c = sc.getCourse();
            m = new HashMap();
            String courseName1Paras;
            m.put("id", sc.getId());
            m.put("studentNum",s.getStudentNum());
            m.put("studentName",s.getStudentName());
            m.put("courseNum",c.getCourseNum());
            m.put("courseName",c.getCourseName());
            if("1".equals(sc.getSituation())) {    //数据库存的是编码，显示是名称
                m.put("situation","正常上课");
            }else {
                m.put("situation","缺勤");
            }
            m.put("reason",sc.getReason());
            if("1".equals(sc.getLeave())) {    //数据库存的是编码，显示是名称
                m.put("leave","已请假");
            }else {
                m.put("leave","未请假");
            }
            m.put("time", DateTimeTool.parseDateTime(sc.getTime(),"yyyy-MM-dd"));  //时间格式转换字符串
            courseName1Paras = "model=courseEdit&id=" + c.getId();
            m.put("courseName",sc.getCourse().getCourseName());
            m.put("courseNameParas",courseName1Paras);
            dataList.add(m);
        }
        return dataList;
    }
    @PostMapping("/attendanceInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse attendanceInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getAttendanceMapList("");
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    //student页面点击查询按钮请求
    //Table界面初始是请求列表的数据，从请求对象里获得前端界面输入的字符串，作为参数传递给方法getStudentMapList，返回所有学生数据，
///     @PostMapping("/attendanceQuery")
///    @PreAuthorize("hasRole('ADMIN')")
///    public DataResponse attendanceQuery(@Valid @RequestBody DataRequest dataRequest){
///        String numName = dataRequest.getString("numName");
///        List dataList = getAttendanceMapList(numName);
///        return CommonMethod.getReturnData(dataList);
///    }
    //  学生信息删除方法
    //Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/attendanceDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse attendanceDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        Attendance s= null;
        Optional<Attendance> op;
        if(id != null) {
            op= attendanceRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            attendanceRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }


    @PostMapping("/attendanceEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse attendanceEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Attendance sc= null;
        Student s;
        Course c;
        Optional<Attendance> op;
        if(id != null) {
            op= attendanceRepository.findById(id);
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
        for(i = 0; i <cList.size();i++) {
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
            form.put("studentId",sc.getStudent().getId());
            form.put("courseId",sc.getCourse().getId());
            form.put("situation",sc.getSituation());
            form.put("time", DateTimeTool.parseDateTime(sc.getTime(),"yyyy-MM-dd"));
            form.put("reason",sc.getReason());
            form.put("leave",sc.getLeave());

        }
        form.put("studentIdList",studentIdList);
        form.put("courseIdList",courseIdList);
        return CommonMethod.getReturnData(form); //这里回传包含学生信息的Map对象
    }
    public synchronized Integer getNewAttendanceId(){
        Integer  id = attendanceRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    @PostMapping("/attendanceEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse attendanceEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form,"id");
        Integer studentId = CommonMethod.getInteger(form,"studentId");
        Integer courseId = CommonMethod.getInteger(form,"courseId");
        String situation = CommonMethod.getString(form,"situation");
        String reason = CommonMethod.getString(form,"reason");
        String leave = CommonMethod.getString(form,"leave");
        Date time = CommonMethod.getDate(form,"time");
        Attendance sc= null;
        Student s= null;
        Course c = null;
        Optional<Attendance> op;
        if(id != null) {
            op= attendanceRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                sc = op.get();
            }
        }
        if(sc == null) {
            sc = new Attendance();   //不存在 创建实体对象
            id = getNewAttendanceId(); //获取鑫的主键，这个是线程同步问题;
            sc.setId(id);  //设置新的id
        }
        sc.setStudent(studentRepository.findById(studentId).get());  //设置属性
        sc.setCourse(courseRepository.findById(courseId).get());
        sc.setSituation(situation);
        sc.setTime(time);
        sc.setReason(reason);
        sc.setLeave(leave);
        attendanceRepository.save(sc);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(sc.getId());  // 将记录的id返回前端
    }


}



