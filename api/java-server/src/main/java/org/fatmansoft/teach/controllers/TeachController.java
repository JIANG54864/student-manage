package org.fatmansoft.teach.controllers;

import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.extend.impl.FSDefaultCacheStore;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.*;
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
import org.fatmansoft.teach.models.FamilyMember;
import org.fatmansoft.teach.models.ContactWay;
import org.fatmansoft.teach.models.PreAdmission;
import org.fatmansoft.teach.models.SocialRel;


import javax.validation.Valid;
import java.io.InputStream;
import java.util.*;

import static org.fatmansoft.teach.util.CommonMethod.getReturnData;

// origins： 允许可访问的域列表
// maxAge:准备响应前的缓存持续的最大时间（以秒为单位）。
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")

public class TeachController {
    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， TeachController 中要使用StudentRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， studentRepository 相当于StudentRepository接口实现对象的一个引用，由框架完成对这个引用的复制，
    // TeachController中的方法可以直接使用
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private IntroduceService introduceService;
    @Autowired
    private DeptRepository deptRepository;
    @Autowired
    private ProvinceRepository provinceRepository;
    @Autowired
    private PracticeRepository practiceRepository;
    @Autowired
    private FamilyMemberRepository familyMemberRepository;
    @Autowired
    private ContactWayRepository contactWayRepository;
    @Autowired
    private PreAdmissionRepository preAdmissionRepository;
    @Autowired
    private SocialRelRepository socialRelRepository;

    @Autowired
    private ResourceLoader resourceLoader;
    private FSDefaultCacheStore fSDefaultCacheStore = new FSDefaultCacheStore();
    private DataRequest dateRequest;


    //getStudentMapList 查询所有学号或姓名与numName相匹配的学生信息，并转换成Map的数据格式存放到List
    //
    // Map 对象是存储数据的集合类，框架会自动将Map转换程用于前后台传输数据的Json对象，Map的嵌套结构和Json的嵌套结构类似，
    //下面方法是生成前端Table数据的示例，List的每一个Map对用显示表中一行的数据
    //Map 每个键值对，对应每一个列的值，如m.put("studentNum",s.getStudentNum())， studentNum这一列显示的是具体的学号的值
    //按照我们测试框架的要求，每个表的主键都是id, 生成表数据是一定要用m.put("id", s.getId());将id传送前端，前端不显示，
    //但在进入编辑页面是作为参数回传到后台.
    public List getStudentMapList(String numName) {
        List dataList = new ArrayList();
        List<Student> sList = studentRepository.findStudentListByNumName(numName);  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Student s;
        Dept d;
        Province p;
        Map m;
        String studentNameParas;
        String familyMemberParas;
        String contactWayParas;
        String preAdmissionParas;
        String socialRelParas;
        for(int i = 0; i < sList.size();i++) {
            s = sList.get(i);
            d = s.getDept();
            p = s.getProvince();
            m = new HashMap();
            m.put("id", s.getId());
            m.put("studentNum",s.getStudentNum());
            studentNameParas = "model=introduce&studentId=" + s.getId();
            familyMemberParas = "model=familyMember&studentId=" + s.getId()+"studentName="+s.getStudentName();
            contactWayParas = "model=contactWay&studentId=" + s.getId()+"studentName="+s.getStudentName();
            preAdmissionParas = "model=preAdmission&studentId=" + s.getId()+"studentName="+s.getStudentName();
            socialRelParas = "model=socialRel&studentId=" + s.getId()+"studentName="+s.getStudentName();
            m.put("studentName",s.getStudentName());
            m.put("studentNameParas",studentNameParas);
            if("1".equals(s.getSex())) {    //数据库存的是编码，显示是名称
                m.put("sex","男");
            }else {
                m.put("sex","女");
            }
            m.put("age",s.getAge());
            m.put("deptName",d.getDeptName());
            m.put("provinceName",p.getProvinceName());
            m.put("birthday", DateTimeTool.parseDateTime(s.getBirthday(),"yyyy-MM-dd"));  //时间格式转换字符串
            //m.put("dept",s.getDept());
            m.put("ethnic",s.getEthnic());
            m.put("s_class",s.getS_class());
            m.put("s_grade",s.getS_grade());
            m.put("profession",s.getProfession());
            m.put("instructor",s.getInstructor());
            //m.put("province",s.getProvince());
            m.put("hobby",s.getHobby());
            m.put("familyMember","家庭信息");
            m.put("familyMemberParas",familyMemberParas);
            m.put("contactWay","联系方式");
            m.put("contactWayParas",contactWayParas);
            m.put("preAdmission","入学前信息");
            m.put("preAdmissionParas",preAdmissionParas);
            m.put("socialRel","社会关系");
            m.put("socialRelParas",socialRelParas);
            dataList.add(m);
        }
        return dataList;
    }



    //student页面初始化方法
    //Table界面初始是请求列表的数据，这里缺省查出所有学生的信息，传递字符“”给方法getStudentMapList，返回所有学生数据，
    @PostMapping("/studentInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse studentInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getStudentMapList("");
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    //student页面点击查询按钮请求
    //Table界面初始是请求列表的数据，从请求对象里获得前端界面输入的字符串，作为参数传递给方法getStudentMapList，返回所有学生数据，
    @PostMapping("/studentQuery")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse studentQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List dataList = getStudentMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    //studentEdit初始化方法
    //studentEdit编辑页面进入时首先请求的一个方法， 如果是Edit,再前台会把对应要编辑的那个学生信息的id作为参数回传给后端，我们通过Integer id = dataRequest.getInteger("id")
    //获得对应学生的id， 根据id从数据库中查出数据，存在Map对象里，并返回前端，如果是添加， 则前端没有id传回，Map 对象数据为空（界面上的数据也为空白）

    @PostMapping("/studentEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse studentEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Student s = null;
        Dept d;
        Province p;
        Optional<Student> op;
        if (id != null) {
            op = studentRepository.findById(id);
            if (op.isPresent()) {
                s = op.get();
            }
        }
        Map m;
        int i;
        List deptIdList = new ArrayList();
        List<Dept> dList = deptRepository.findAll();
        for(i=0;i<dList.size();i++){
            d=dList.get(i);
            m = new HashMap();
            m.put("label",d.getDeptName());
            m.put("value",d.getId());
            deptIdList.add(m);
        }
        List provinceIdList = new ArrayList();
        List<Province> pList = provinceRepository.findAll();
        for(i=0;i<pList.size();i++){
            p=pList.get(i);
            m = new HashMap();
            m.put("label",p.getProvinceName());
            m.put("value",p.getId());
            provinceIdList.add(m);
        }
        Map form = new HashMap();
        form.put("deptId","");
        form.put("provinceId","");
        if (s != null) {
            form.put("id", s.getId());
            form.put("studentNum", s.getStudentNum());
            form.put("studentName", s.getStudentName());
            form.put("sex", s.getSex());  //这里不需要转换
            form.put("age", s.getAge());
            form.put("s_grade", s.getS_grade());
            form.put("s_class", s.getS_class());
            form.put("ethnic", s.getEthnic());
            form.put("profession", s.getProfession());
            form.put("instructor", s.getInstructor());
            form.put("provinceId", s.getProvince().getId());
            form.put("provinceName", s.getProvince().getProvinceName());
            form.put("deptName", s.getDept().getDeptName());
            form.put("deptId",s.getDept().getId());
            form.put("hobby",s.getHobby());
            form.put("birthday", DateTimeTool.parseDateTime(s.getBirthday(),"yyyy-MM-dd")); //这里需要转换为字符串
        }
        form.put("deptIdList",deptIdList);
        form.put("provinceIdList",provinceIdList);
        return CommonMethod.getReturnData(form); //这里回传包含学生信息的Map对象
    }

    //  学生信息提交按钮方法
    //相应提交请求的方法，前端把所有数据打包成一个Json对象作为参数传回后端，后端直接可以获得对应的Map对象form, 再从form里取出所有属性，复制到
    //实体对象里，保存到数据库里即可，如果是添加一条记录， id 为空，这是先 new Student 计算新的id， 复制相关属性，保存，如果是编辑原来的信息，
    //id 不为空。则查询出实体对象，复制相关属性，保存后修改数据库信息，永久修改
    public synchronized Integer getNewStudentId() {
        Integer
                id = studentRepository.getMaxId();  // 查询最大的id
        if (id == null)
            id = 1;
        else
            id = id + 1;
        return id;
    }

    ;

    @PostMapping("/studentEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse studentEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form, "id");
        String studentNum = CommonMethod.getString(form, "studentNum");  //Map 获取属性的值
        String studentName = CommonMethod.getString(form, "studentName");
        String sex = CommonMethod.getString(form, "sex");
        Integer age = CommonMethod.getInteger(form, "age");
        Date birthday = CommonMethod.getDate(form, "birthday");
        Integer s_grade = CommonMethod.getInteger(form, "s_grade");
        Integer s_class = CommonMethod.getInteger(form, "s_class");
        String profession = CommonMethod.getString(form, "profession");
        String instructor = CommonMethod.getString(form, "instructor");
        Integer provinceId = CommonMethod.getInteger(form,"provinceId");
        String provinceName = CommonMethod.getString(form,"provinceName");
        Integer deptId = CommonMethod.getInteger(form,"deptId");
        String deptName = CommonMethod.getString(form,"deptName");
        String ethnic = CommonMethod.getString(form,"ethnic");
        //String hobby = CommonMethod.getString(form,"hobby");
        Student s = null;
        Optional<Student> op;
        if (id != null) {
            op = studentRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                s = op.get();
            }
        }
        if (s == null) {
            s = new Student();   //不存在 创建实体对象
            id = getNewStudentId(); //获取鑫的主键，这个是线程同步问题;
            s.setId(id);  //设置新的id
        }
        s.setStudentNum(studentNum);  //设置属性
        s.setStudentName(studentName);
        s.setSex(sex);
        s.setAge(age);
        s.setBirthday(birthday);
        s.setSex(sex);
        s.setDept(deptRepository.findById(deptId).get());
        s.setDeptName(deptName);
        s.setEthnic(ethnic);
        s.setInstructor(instructor);
        s.setS_class(s_class);
        s.setS_grade(s_grade);
        s.setProfession(profession);
        s.setProvince(provinceRepository.findById(provinceId).get());
        s.setProvinceName(provinceName);
        //s.setHobby(hobby);


        studentRepository.save(s);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(s.getId());  // 将记录的id返回前端
    }

    //  学生信息删除方法
    //Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/studentDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse studentDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        Student s = null;
        Optional<Student> op;
        if (id != null) {
            op = studentRepository.findById(id);   //查询获得实体对象
            if (op.isPresent()) {
                s = op.get();
            }
        }
        if (s != null) {
            studentRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }

    //  学生个人简历页面
    //在系统在主界面内点击个人简历，后台准备个人简历所需要的各类数据组成的段落数据，在前端显示
    @PostMapping("/getStudentIntroduceData")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse getStudentIntroduceData(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        Map data = introduceService.getIntroduceDataMap(studentId);
        return CommonMethod.getReturnData(data);  //返回前端个人简历数据
    }

    public ResponseEntity<StreamingResponseBody> getPdfDataFromHtml(String htmlContent) {
        try {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(htmlContent, null);
            builder.useFastMode();
            builder.useCacheStore(PdfRendererBuilder.CacheStore.PDF_FONT_METRICS, fSDefaultCacheStore);
            Resource resource = resourceLoader.getResource("classpath:font/SourceHanSansSC-Regular.ttf");
            InputStream fontInput = resource.getInputStream();
            builder.useFont(new FSSupplier<InputStream>() {
                @Override
                public InputStream supply() {
                    return fontInput;
                }
            }, "SourceHanSansSC");
            StreamingResponseBody stream = outputStream -> {
                builder.toStream(outputStream);
                builder.run();
            };

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(stream);

        }
        catch (Exception e) {
            return  ResponseEntity.internalServerError().build();
        }
    }
    @PostMapping("/getStudentIntroducePdf")
    @PreAuthorize(" hasRole('ADMIN')")
    public ResponseEntity<StreamingResponseBody> getStudentIntroducePdf(@Valid @RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        Map data = introduceService.getIntroduceDataMap(studentId);
        String content= "<!DOCTYPE html>";
        content += "<html>";
        content += "<head>";
        content += "<style>";
        content += "html { font-family: \"SourceHanSansSC\", \"Open Sans\";}";
        content += "</style>";
        content += "<meta charset='UTF-8' />";
        content += "<title>Insert title here</title>";
        content += "</head>";

        String myName = (String) data.get("myName");
        String overview = (String) data.get("overview");
        List<Map> attachList = (List) data.get("attachList");

//        content += getHtmlString();
        content += "<body>";

        content += "<table style='width: 100%;'>";
        content += "   <thead >";
        content += "     <tr style='text-align: center;font-size: 32px;font-weight:bold;'>";
        content += "        "+myName+" </tr>";
        content += "   </thead>";
        content += "   </table>";

        content += "<table style='width: 100%;'>";
        content += "   <thead >";
        content += "     <tr style='text-align: center;font-size: 32px;font-weight:bold;'>";
        content += "        "+overview+" </tr>";
        content += "   </thead>";
        content += "   </table>";

        content += "<table style='width: 100%;border-collapse: collapse;border: 1px solid black;'>";
        content +=   " <tbody>";

        for(int i = 0; i <attachList.size(); i++ ){
            content += "     <tr style='text-align: center;border: 1px solid black;font-size: 14px;'>";
            content += "      "+attachList.get(i).get("title")+" ";
            content += "     </tr>";
            content += "     <tr style='text-align: center;border: 1px solid black; font-size: 14px;'>";
            content += "            "+attachList.get(i).get("content")+" ";
            content += "     </tr>";
        }
        content +=   " </tbody>";
        content += "   </table>";

        content += "</body>";
        content += "</html>";
        return getPdfDataFromHtml(content);
    }

    public List getScoreMapList(String numName) {
        List dataList = new ArrayList();
        List<Score> sList = scoreRepository.findAll();  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        Score sc;
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
            m.put("mark",sc.getMark());
            dataList.add(m);
        }
        return dataList;
    }
    @PostMapping("/scoreInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse scoreInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getScoreMapList("");
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    //  学生信息删除方法
    //Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/scoreDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse scoreDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        Score s= null;
        Optional<Score> op;
        if(id != null) {
            op= scoreRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
            }
        }
        if(s != null) {
            scoreRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }


    @PostMapping("/scoreEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse scoreEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Score sc= null;
        Student s;
        Course c;
        Optional<Score> op;
        if(id != null) {
            op= scoreRepository.findById(id);
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
            form.put("mark",sc.getMark());
        }
        form.put("studentIdList",studentIdList);
        form.put("courseIdList",courseIdList);
        return CommonMethod.getReturnData(form); //这里回传包含学生信息的Map对象
    }
    public synchronized Integer getNewScoreId(){
        Integer  id = scoreRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    @PostMapping("/scoreEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse scoreEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form,"id");
        Integer studentId = CommonMethod.getInteger(form,"studentId");
        Integer courseId = CommonMethod.getInteger(form,"courseId");
        Integer mark = CommonMethod.getInteger(form,"mark");
        Score sc= null;
        Student s= null;
        Course c = null;
        Optional<Score> op;
        if(id != null) {
            op= scoreRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                sc = op.get();
            }
        }
        if(sc == null) {
            sc = new Score();   //不存在 创建实体对象
            id = getNewScoreId(); //获取鑫的主键，这个是线程同步问题;
            sc.setId(id);  //设置新的id
        }
        sc.setStudent(studentRepository.findById(studentId).get());  //设置属性
        sc.setCourse(courseRepository.findById(courseId).get());
        sc.setMark(mark);
        scoreRepository.save(sc);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(sc.getId());  // 将记录的id返回前端
    }



    public List getPracticeMapList(String numName) {
        List dataList = new ArrayList();
        List<Practice> sList = practiceRepository.findPracticeBynumName(numName);  //数据库查询操作

        if(sList == null || sList.size() == 0)
            return dataList;
        Practice p;
        Student s;
        Map m;
        String pracnameParas,studentNameParas;
        for(int i = 0; i < sList.size();i++) {
            p = sList.get(i);
            s = p.getStudent();
            m = new HashMap();
            m.put("id", p.getId());
            m.put("studentNum",s.getStudentNum());
            m.put("studentName",s.getStudentName());
            m.put("pracname",p.getPracname());
            pracnameParas = "model=introduce&pracnameId=" + p.getId();
            studentNameParas = "model=introduce&pracnameId=" + s.getId();
            m.put("studentName",s.getStudentName());
            m.put("pracnameParas",pracnameParas);
            if("1".equals(p.getPractype())) {    //数据库存的是编码，显示是名称
                m.put("practype","社会实践");
            }else if("2".equals(p.getPractype())) {    //数据库存的是编码，显示是名称
                m.put("practype","学科竞赛");
            }else if("3".equals(p.getPractype())) {    //数据库存的是编码，显示是名称
                m.put("practype","科技成果");
            }else if("4".equals(p.getPractype())) {    //数据库存的是编码，显示是名称
                m.put("practype","培训讲座");
            }else if("5".equals(p.getPractype())) {    //数据库存的是编码，显示是名称
                m.put("practype","创新项目");
            }else{    //数据库存的是编码，显示是名称
                m.put("practype","校外实习");
            }

            if("1".equals(p.getPraclevel())) {    //数据库存的是编码，显示是名称
                m.put("praclevel","院级");
            }else if("2".equals(p.getPraclevel())) {    //数据库存的是编码，显示是名称
                m.put("praclevel","校级");
            }else {    //数据库存的是编码，显示是名称
                m.put("praclevel", "省级");
            }
            m.put("pracwin",p.getPracwin());



            dataList.add(m);
        }
        return dataList;
    }


    public List getFamilyMemberMapList(String numName) {
        List dataList = new ArrayList();
        List<FamilyMember> sList = familyMemberRepository.findAll();  //数据库查询操作
        if(sList == null || sList.size() == 0)
            return dataList;
        FamilyMember fb;
        Student s;
        Map m;
        for(int i = 0; i < sList.size();i++) {
            fb = sList.get(i);
            s = fb.getStudent();
            m = new HashMap();
            m.put("id", fb.getId());
            m.put("studentNum",s.getStudentNum());
            m.put("name",s.getStudentName());
            if("1".equals(s.getSex())) {    //数据库存的是编码，显示是名称
                m.put("sex","男");
            }else {
                m.put("sex","女");
            }
            m.put("rel",fb.getRel());
            m.put("relTel",fb.getRelTel());
            m.put("relWorkAdd",fb.getRelWorkAdd());
            m.put("relName",fb.getRelName());
            dataList.add(m);
        }
        return dataList;
    }

    @PostMapping("/familyMemberInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse familyMemberInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getFamilyMemberMapList("");
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    /*@PostMapping("/familyMemberQuery")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse familyMemberQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        //Integer studentNum = dataRequest.getInteger("studentNum");
        List dataList = getFamilyMemberMapList(numName);
        return getReturnData(dataList);  //按照测试框架规范会送Map的list
    }*/

    //  学生信息删除方法
    //Student页面的列表里点击删除按钮则可以删除已经存在的学生信息， 前端会将该记录的id 回传到后端，方法从参数获取id，查出相关记录，调用delete方法删除
    @PostMapping("/familyMemberDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse familyMemberDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        FamilyMember f= null;
        Optional<FamilyMember> op;
        if(id != null) {
            op= familyMemberRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                f = op.get();
            }
        }
        if(f != null) {
            familyMemberRepository.delete(f);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }


    @PostMapping("/familyMemberEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse familyMemberEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        FamilyMember fb= null;
        Student s;
        Optional<FamilyMember> op;
        if(id != null) {
            op= familyMemberRepository.findById(id);
            if(op.isPresent()) {
                fb = op.get();
            }
        }
        Map m;
        int i;
        List studentIdList = new ArrayList();
        List<Student> sList = studentRepository.findAll();
        for(i = 0;i<sList.size();i++){
            s=sList.get(i);
            m = new HashMap();
            m.put("label",s.getStudentName());
            m.put("value",s.getId());
            studentIdList.add(m);
        }
        Map form = new HashMap();
        form.put("studentId","");
        if(fb  != null) {
            form.put("id",fb.getId());
            form.put("studentId",fb.getStudent().getId());
            form.put("name",fb.getStudent().getStudentName());
            form.put("sex",fb.getSex());
            form.put("rel",fb.getRel());
            form.put("relTel",fb.getRelTel());
            form.put("relWorkAdd",fb.getRelWorkAdd());
            form.put("relName",fb.getRelName());
        }
        form.put("studentIdList",studentIdList);
        return CommonMethod.getReturnData(form); //这里回传包含学生信息的Map对象
    }

    public synchronized Integer getNewFamilyMemberId(){
        Integer  id = familyMemberRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };

    @PostMapping("/familyMemberEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse familyMemberEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form,"id");
        Integer studentId = CommonMethod.getInteger(form,"studentId");
        String name= CommonMethod.getString(form,"name");
        //String sex = CommonMethod.getString(form,"sex");
        String rel = CommonMethod.getString(form,"rel");
        String relWorkAdd = CommonMethod.getString(form,"relWorkAdd");
        Integer relTel = CommonMethod.getInteger(form,"relTel");
        String relName = CommonMethod.getString(form,"relName");

        FamilyMember fb= null;
        //Student s= null;
        Optional<FamilyMember> op;
        if(id != null) {
            op= familyMemberRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                fb = op.get();
            }
        }
        if(fb == null) {
            fb = new FamilyMember();   //不存在 创建实体对象
            id = getNewFamilyMemberId(); //获取鑫的主键，这个是线程同步问题;
            fb.setId(id);  //设置新的id
        }
        fb.setStudent(studentRepository.findById(studentId).get());  //设置属性
        fb.setName(name);
        //fb.setId(studentId);
        //fb.setSex(sex);
        fb.setRel(rel);
        fb.setRelTel(relTel);
        fb.setRelWorkAdd(relWorkAdd);
        fb.setRelName(relName);
        familyMemberRepository.save(fb);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(fb.getId());  // 将记录的id返回前端
    }

    public List getContactWayMapList(String numName) {
        List dataList = new ArrayList();
        List<ContactWay> sList = contactWayRepository.findAll();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        ContactWay c;
        Student s;
        Map m;
        for (int i = 0; i < sList.size(); i++) {
            c = sList.get(i);
            s = c.getStudent();
            m = new HashMap();
            m.put("id", s.getId());
            m.put("studentNum", s.getStudentNum());
            m.put("name", s.getStudentName());
            if ("1".equals(s.getSex())) {    //数据库存的是编码，显示是名称
                m.put("sex", "男");
            } else {
                m.put("sex", "女");
            }
            m.put("address", c.getAddress());
            m.put("tel", c.getTel());
            m.put("email", c.getEmail());
            m.put("QQNum", c.getQQNum());
            dataList.add(m);
        }
        return dataList;
    }


    @PostMapping("/contactWayInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse contactWayInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getContactWayMapList("");
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }


    @PostMapping("/contactWayEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse contactWayEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        ContactWay c = null;
        Student s;
        Optional<ContactWay> op;
        if (id != null) {
            op = contactWayRepository.findById(id);
            if (op.isPresent()) {
                c = op.get();
            }
        }
        Map m;
        int i;
        List studentIdList = new ArrayList();
        List<Student> sList = studentRepository.findAll();
        for(i = 0;i<sList.size();i++){
            s=sList.get(i);
            m = new HashMap();
            m.put("label",s.getStudentName());
            m.put("value",s.getId());
            studentIdList.add(m);
        }
        Map form = new HashMap();
        form.put("studentId","");
        if (c != null) {
            form.put("id", c.getId());
            form.put("studentNum", c.getStudent().getId());
            form.put("name", c.getStudent().getStudentName());
            form.put("sex", c.getSex());  //这里不需要转换
            form.put("tel", c.getTel());
            form.put("QQNum", c.getQQNum());
            form.put("address", c.getAddress());
            form.put("email", c.getEmail());
        }
        form.put("studentIdList",studentIdList);
        return CommonMethod.getReturnData(form); //这里回传包含学生信息的Map对象
    }

    public synchronized Integer getNewContactWayId() {
        Integer
                id = contactWayRepository.getMaxId();  // 查询最大的id
        if (id == null)
            id = 1;
        else
            id = id + 1;
        return id;
    }

    @PostMapping("/contactWayEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse contactWayEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form, "id");
        Integer studentId = CommonMethod.getInteger(form,"studentId");  //Map 获取属性的值
        //String studentName = CommonMethod.getString(form, "name");
        //String sex = CommonMethod.getString(form, "sex");
        String address = CommonMethod.getString(form,"address");
        Integer tel = CommonMethod.getInteger(form,"tel");
        Integer QQNum = CommonMethod.getInteger(form,"QQNum");
        String email = CommonMethod.getString(form,"email");
        ContactWay s = null;
        Optional<ContactWay> op;
        if (id != null) {
            op = contactWayRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                s = op.get();
            }
        }
        if (s == null) {
            s = new ContactWay();   //不存在 创建实体对象
            id = getNewContactWayId(); //获取鑫的主键，这个是线程同步问题;
            s.setId(id);  //设置新的id
        }
        s.setStudent(studentRepository.findById(studentId).get());//设置属性
        s.setAddress(address);
        s.setEmail(email);
        s.setQQNum(QQNum);
        s.setTel(tel);

        contactWayRepository.save(s);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(s.getId());  // 将记录的id返回前端
    }

    @PostMapping("/contactWayDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse contactWayDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        ContactWay s = null;
        Optional<ContactWay> op;
        if (id != null) {
            op = contactWayRepository.findById(id);   //查询获得实体对象
            if (op.isPresent()) {
                s = op.get();
            }
        }
        if (s != null) {
            contactWayRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();   //通知前端操作正常
    }

    public List getPreAdmissionMapList(String numName) {
        List dataList = new ArrayList();
        List<PreAdmission> sList = preAdmissionRepository.findAll();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        PreAdmission c;
        Student s;
        Map m;
        //String studentNameParas;
        for (int i = 0; i < sList.size(); i++) {
            c = sList.get(i);
            s = c.getStudent();
            m = new HashMap();
            m.put("id", s.getId());
            m.put("studentNum", s.getStudentNum());
            m.put("name", s.getStudentName());
            if ("1".equals(s.getSex())) {    //数据库存的是编码，显示是名称
                m.put("sex", "男");
            } else {
                m.put("sex", "女");
            }
            m.put("ceeScore", c.getCeeScore());
            m.put("examReg", c.getExamReg());
            m.put("highSchool", c.getHighSchool());
            dataList.add(m);
        }
        return dataList;
    }


    @PostMapping("/preAdmissionInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse preAdmissionInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getPreAdmissionMapList("");
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/preAdmissionQuery")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse preAdmissionQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List dataList = getPreAdmissionMapList(numName);
        return getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/preAdmissionEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse preAdmissionEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        PreAdmission c = null;
        Student s;
        Optional<PreAdmission> op;
        if (id != null) {
            op = preAdmissionRepository.findById(id);
            if (op.isPresent()) {
                c = op.get();
            }
        }
        Map m;
        int i;
        List studentIdList = new ArrayList();
        List<Student> sList = studentRepository.findAll();
        for(i = 0;i<sList.size();i++){
            s=sList.get(i);
            m = new HashMap();
            m.put("label",s.getStudentName());
            m.put("value",s.getId());
            studentIdList.add(m);
        }
        Map form = new HashMap();
        form.put("studentId","");
        if (c != null) {
            form.put("id", c.getId());
            form.put("studentNum", c.getStudent().getId());
            form.put("name", c.getStudent().getStudentName());
            form.put("sex", c.getSex());  //这里不需要转换
            form.put("ceeScore", c.getCeeScore());
            form.put("examReg", c.getExamReg());
            form.put("highSchool", c.getHighSchool());
        }
        form.put("studentIdList",studentIdList);
        return CommonMethod.getReturnData(form); //这里回传包含学生信息的Map对象
    }

    public synchronized Integer getNewPreAdmissionId() {
        Integer
                id = preAdmissionRepository.getMaxId();  // 查询最大的id
        if (id == null)
            id = 1;
        else
            id = id + 1;
        return id;
    }

    @PostMapping("/preAdmissionEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse preAdmissionEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form, "id");
        Integer studentId = CommonMethod.getInteger(form,"studentId");  //Map 获取属性的值
        //String studentName = CommonMethod.getString(form, "name");
        //String sex = CommonMethod.getString(form, "sex");
        Integer ceeScore = CommonMethod.getInteger(form,"ceeScore");
        String highSchool = CommonMethod.getString(form,"highSchool");
        Integer examReg = CommonMethod.getInteger(form,"examReg");
        PreAdmission s = null;
        Optional<PreAdmission> op;
        if (id != null) {
            op = preAdmissionRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                s = op.get();
            }
        }
        if (s == null) {
            s = new PreAdmission();   //不存在 创建实体对象
            id = getNewPreAdmissionId(); //获取鑫的主键，这个是线程同步问题;
            s.setId(id);  //设置新的id
        }
        s.setStudent(studentRepository.findById(studentId).get());//设置属性
        s.setCeeScore(ceeScore);
        s.setHighSchool(highSchool);
        s.setExamReg(examReg);

        preAdmissionRepository.save(s);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(s.getId());  // 将记录的id返回前端
    }

    @PostMapping("/preAdmissionDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse preAdmissionDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        PreAdmission s = null;
        Optional<PreAdmission> op;
        if (id != null) {
            op = preAdmissionRepository.findById(id);   //查询获得实体对象
            if (op.isPresent()) {
                s = op.get();
            }
        }
        if (s != null) {
            preAdmissionRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();   //通知前端操作正常
    }

    public List getSocialRelMapList(String numName) {
        List dataList = new ArrayList();
        List<SocialRel> sList = socialRelRepository.findAll();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        SocialRel c;
        Student s;
        Map m;
        //String studentNameParas;
        for (int i = 0; i < sList.size(); i++) {
            c = sList.get(i);
            s = c.getStudent();
            m = new HashMap();
            m.put("id", s.getId());
            m.put("studentNum", s.getStudentNum());
            m.put("name", s.getStudentName());
            if ("1".equals(s.getSex())) {    //数据库存的是编码，显示是名称
                m.put("sex", "男");
            } else {
                m.put("sex", "女");
            }
            m.put("leagueTime", DateTimeTool.parseDateTime(c.getLeagueTime(),"yyyy-MM-dd"));  //时间格式转换字符串
            m.put("partyTime", DateTimeTool.parseDateTime(c.getPartyTime(),"yyyy-MM-dd"));
            m.put("polStatus", c.getPolStatus());
            dataList.add(m);
        }
        return dataList;
    }


    @PostMapping("/socialRelInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse socialRelInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getSocialRelMapList("");
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }


    @PostMapping("/socialRelEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse socialRelEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        SocialRel c = null;
        Student s;
        Optional<SocialRel> op;
        if (id != null) {
            op = socialRelRepository.findById(id);
            if (op.isPresent()) {
                c = op.get();
            }
        }
        Map m;
        int i;
        List studentIdList = new ArrayList();
        List<Student> sList = studentRepository.findAll();
        for(i = 0;i<sList.size();i++){
            s=sList.get(i);
            m = new HashMap();
            m.put("label",s.getStudentName());
            m.put("value",s.getId());
            studentIdList.add(m);
        }
        Map form = new HashMap();
        form.put("studentId","");
        if (c != null) {
            form.put("id", c.getId());
            form.put("studentNum", c.getStudent().getId());
            form.put("name", c.getStudent().getStudentName());
            form.put("sex", c.getSex());  //这里不需要转换
            form.put("polStatus", c.getPolStatus());
            form.put("leagueTime", DateTimeTool.parseDateTime(c.getLeagueTime(),"yyyy-MM-dd")); //这里需要转换为字符串
            form.put("partyTime", DateTimeTool.parseDateTime(c.getPartyTime(),"yyyy-MM-dd"));
        }
        form.put("studentIdList",studentIdList);
        return CommonMethod.getReturnData(form); //这里回传包含学生信息的Map对象
    }

    public synchronized Integer getNewSocialRelId() {
        Integer
                id = socialRelRepository.getMaxId();  // 查询最大的id
        if (id == null)
            id = 1;
        else
            id = id + 1;
        return id;
    }

    @PostMapping("/socialRelEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse socialRelEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form, "id");
        Integer studentId = CommonMethod.getInteger(form,"studentId");  //Map 获取属性的值
        //String studentName = CommonMethod.getString(form, "name");
        //String sex = CommonMethod.getString(form, "sex");
        String polStatus = CommonMethod.getString(form,"polStatus");
        Date leagueTime = CommonMethod.getDate(form,"leagueTime");
        Date partyTime = CommonMethod.getDate(form,"partyTime");
        SocialRel s = null;
        Optional<SocialRel> op;
        if (id != null) {
            op = socialRelRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if (op.isPresent()) {
                s = op.get();
            }
        }
        if (s == null) {
            s = new SocialRel();   //不存在 创建实体对象
            id = getNewSocialRelId(); //获取鑫的主键，这个是线程同步问题;
            s.setId(id);  //设置新的id
        }
        s.setStudent(studentRepository.findById(studentId).get());//设置属性
        s.setLeagueTime(leagueTime);
        s.setPartyTime(partyTime);
        s.setPolStatus(polStatus);


        socialRelRepository.save(s);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(s.getId());  // 将记录的id返回前端
    }

    @PostMapping("/socialRelDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse socialRelDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        SocialRel s = null;
        Optional<SocialRel> op;
        if (id != null) {
            op = socialRelRepository.findById(id);   //查询获得实体对象
            if (op.isPresent()) {
                s = op.get();
            }
        }
        if (s != null) {
            socialRelRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();   //通知前端操作正常
    }

    public List getActivityMapList(String numName) {
        List dataList = new ArrayList();
        List<Activity> sList = activityRepository.findAll();  //数据库查询操作

        if(sList == null || sList.size() == 0)
            return dataList;
        Activity p;
        Student s;
        Map m;
        //String activityParas,studentNameParas;
        for(int i = 0; i < sList.size();i++) {
            p = sList.get(i);
            s = p.getStudent();
            m = new HashMap();
            m.put("id", p.getId());
            m.put("studentNum",s.getStudentNum());
            m.put("studentName",s.getStudentName());
            m.put("activityName",p.getActivityName());
            m.put("activityTime", DateTimeTool.parseDateTime(p.getActivityTime(),"yyyy-MM-dd"));
            m.put("activitySite",p.getActivitySite());
            dataList.add(m);
        }
        return dataList;
    }
    //activity页面初始化方法
    //Table界面初始是请求列表的数据，这里缺省查出所有活动的信息，传递字符“”给方法，返回所有学生数据，
    @PostMapping("/activityInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse activityInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getActivityMapList("");
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    // 活动删除方法
    @PostMapping("/activityDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse activityDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        Activity p= null;
        Optional<Activity> op;
        if(id != null) {
            op= activityRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                p = op.get();
            }
        }
        if(p != null) {
            activityRepository.delete(p);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }
    //活动编辑页面
    @PostMapping("/activityEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse activityEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Activity p = null;
        Student s;
        Optional<Activity> op;
        if (id != null) {
            op = activityRepository.findById(id);
            if (op.isPresent()) {
                p = op.get();
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
        if (p != null) {
            form.put("id", p.getId());
            form.put("studentId",p.getStudent().getId());
            form.put("name",p.getStudent().getStudentName());
            //form.put("studentNum",s.getStudentNum());
            //form.put("studentName",s.getStudentName());
            form.put("activityName",p.getActivityName());  //这里不需要转换
            form.put("activityTime", DateTimeTool.parseDateTime(p.getActivityTime(),"yyyy-MM-dd")); //这里需要转换为字符串
            form.put("activitySite",p.getActivitySite());
        }
        form.put("studentIdList",studentIdList);
        return CommonMethod.getReturnData(form); //这里回传包含学生信息的Map对象
    }
    public synchronized Integer getNewActivityId(){
        Integer  id = activityRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };

    @PostMapping("/activityEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse activityEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form,"id");
        Integer studentId = CommonMethod.getInteger(form,"studentId");
        String activityName = CommonMethod.getString(form,"activityName");
        Date activityTime = CommonMethod.getDate(form,"activityTime");
        String activitySite = CommonMethod.getString(form,"activitySite");
        Activity a= null;
        //Student s= null;
        Optional<Activity> op;
        if(id != null) {
            op= activityRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                a = op.get();
            }
        }
        if(a == null) {
            a = new Activity();   //不存在 创建实体对象
            id = getNewActivityId(); //获取鑫的主键，这个是线程同步问题;
            a.setId(id);  //设置新的id
        }
        a.setStudent(studentRepository.findById(studentId).get());  //设置属性
        a.setActivityName(activityName);
        a.setActivityTime(activityTime);
        a.setActivitySite(activitySite);
        activityRepository.save(a);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(a.getId());  // 将记录的id返回前端
    }

    //practice页面初始化方法
    //Table界面初始是请求列表的数据，这里缺省查出所有活动的信息，传递字符“”给方法，返回所有学生数据，
    @PostMapping("/practiceInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse practiceInit(@Valid @RequestBody DataRequest dataRequest) {
        List dataList = getPracticeMapList("");
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    //活动信息提交按钮方法
    public synchronized Integer getNewPracticeId(){
        Integer
                id = practiceRepository.getMaxId();  // 查询最大的id
        if(id == null)
            id = 1;
        else
            id = id+1;
        return id;
    };
    @PostMapping("/practiceEditSubmit")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse practiceEditSubmit(@Valid @RequestBody DataRequest dataRequest) {
        Map form = dataRequest.getMap("form"); //参数获取Map对象
        Integer id = CommonMethod.getInteger(form,"id");
        Integer studentId = CommonMethod.getInteger(form,"studentId");
        String pracname = CommonMethod.getString(form,"pracname");  //Map 获取属性的值
        String practype = CommonMethod.getString(form,"practype");
        String praclevel = CommonMethod.getString(form,"praclevel");
        String pracwin = CommonMethod.getString(form,"pracwin");


        Practice p= null;
        Student s= null;
        Optional<Practice> op;
        if(id != null) {
            op= practiceRepository.findById(id);  //查询对应数据库中主键为id的值的实体对象
            if(op.isPresent()) {
                p = op.get();
            }
        }
        if(p == null) {
            p = new Practice();   //不存在 创建实体对象
            id = getNewPracticeId(); //获取鑫的主键，这个是线程同步问题;
            p.setId(id);  //设置新的id
        }
        p.setStudent(studentRepository.findById(studentId).get());
        p.setPracname(pracname);  //设置属性
        p.setPractype(practype);
        p.setPraclevel(praclevel);
        p.setPracwin(pracwin);

        practiceRepository.save(p);  //新建和修改都调用save方法
        return CommonMethod.getReturnData(p.getId());  // 将记录的id返回前端
    }
    //点击查询按钮请求
    //Table界面初始是请求列表的数据，从请求对象里获得前端界面输入的字符串，作为参数传递给方法getStudentMapList，返回所有学生数据，
    @PostMapping("/practiceQuery")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse practiceQuery(@Valid @RequestBody DataRequest dataRequest) {
        String pracname= dataRequest.getString("pracname");
        List dataList = getPracticeMapList(pracname);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }
    // 活动删除方法
    @PostMapping("/practiceDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse practiceDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        Practice p= null;
        Optional<Practice> op;
        if(id != null) {
            op= practiceRepository.findById(id);   //查询获得实体对象
            if(op.isPresent()) {
                p = op.get();
            }
        }
        if(p != null) {
            practiceRepository.delete(p);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }
    //活动编辑页面
    @PostMapping("/practiceEditInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse practiceEditInit(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");
        Practice p = null;
        Student s;
        Optional<Practice> op;
        if (id != null) {
            op = practiceRepository.findById(id);
            if (op.isPresent()) {
                p = op.get();
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
        if (p != null) {
            form.put("id", p.getId());
            form.put("studentId",p.getStudent().getId());
            form.put("pracname", p.getPracname());
            form.put("practype", p.getPractype());
            form.put("praclevel", p.getPraclevel());  //这里不需要转换
            form.put("pracwin", p.getPracwin());
        }
        form.put("studentIdList",studentIdList);
        return CommonMethod.getReturnData(form); //这里回传包含学生信息的Map对象
    }

//********************************************************************************************************************************


}

