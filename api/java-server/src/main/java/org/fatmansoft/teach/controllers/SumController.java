package org.fatmansoft.teach.controllers;

import com.openhtmltopdf.extend.impl.FSDefaultCacheStore;
import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.SumRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.repository.ScoreRepository;
import org.fatmansoft.teach.service.IntroduceService;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.fatmansoft.teach.repository.PracticeRepository;
import org.fatmansoft.teach.models.Practice;
import org.fatmansoft.teach.models.Activity;
import org.fatmansoft.teach.repository.ActivityRepository;




import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")

public class SumController {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private SumRepository sumRepository;
    @Autowired
    private PracticeRepository practiceRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private IntroduceService introduceService;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private ResourceLoader resourceLoader;
    private FSDefaultCacheStore fSDefaultCacheStore = new FSDefaultCacheStore();
    private DataRequest dateRequest;

    public List getSumMapList(String numName) {
        List dataList = new ArrayList();
        List<Sum> sList = sumRepository.findAll();  //数据库查询操作
        if (sList == null || sList.size() == 0)
            return dataList;
        Sum c;
        Student s;
        Map m;
        String nameParas;
        for (int i = 0; i < sList.size(); i++) {
            c = sList.get(i);
            s = c.getStudent();
            m = new HashMap();
            m.put("id", s.getId());
            Integer stuId = s.getId();
            m.put("studentNum", s.getStudentNum());
            nameParas = "model=introduce&studentId=" + s.getId();
            m.put("name",s.getStudentName());
            m.put("nameParas",nameParas);
            if ("1".equals(s.getSex())) {    //数据库存的是编码，显示是名称
                m.put("sex", "男");
            } else {
                m.put("sex", "女");
            }

            Integer solAcScore=0;
            Integer sciScore=0;
            Integer actScore=0;
            List<Activity> acList = activityRepository.findByStudentId(stuId);
            StringBuilder solResult = new StringBuilder();
            String solRe = null;
            StringBuilder sciResult = new StringBuilder();
            String sciRe = null;
            StringBuilder acResult = new StringBuilder();
            String acRe = null;
            List<Practice> spList = practiceRepository.findByStudentId1(stuId);
            for(int k = 0;k<spList.size();k++){
                Practice pr = spList.get(k);
                String strr = "  ";
                if((pr.getPractype()).equals("1")||(pr.getPractype()).equals("776")){
                    solResult.append(pr.getPracname()).append(strr);
                    if((pr.getPraclevel()).equals("1"))   solAcScore+=3;
                    if((pr.getPraclevel()).equals("2"))   solAcScore+=2;
                    if((pr.getPraclevel()).equals("3"))   solAcScore+=1;//按照不同的立项等级进行加分
                }

                if((pr.getPractype()).equals("3")||(pr.getPractype()).equals("4")||(pr.getPractype()).equals("5")||(pr.getPractype()).equals("2")){
                    sciResult.append(pr.getPracname()).append(strr);
                    if((pr.getPraclevel()).equals("1"))   sciScore+=3;
                    if((pr.getPraclevel()).equals("2"))   sciScore+=2;
                    if((pr.getPraclevel()).equals("3"))   sciScore+=1;//按照不同的立项等级进行加分
                }
            }
            for(int w = 0;w<acList.size();w++){
                Activity ac = acList.get(w);
                acResult.append(ac.getActivityName()).append("  ");
                actScore++;
            }
            solRe = solResult.toString();
            m.put("solAc",solRe);
            sciRe = sciResult.toString();
            m.put("Sci",sciRe);
            acRe = acResult.toString();
            m.put("Act",acRe);
            solAcScore=solAcScore*10;
            sciScore=sciScore*8;
            actScore=actScore*10;


            List<Score> scList = scoreRepository.findByStudentId1(stuId);
            Double sum=0.00;//所有加和
            Double summ=0.00;



            int num = scList.size();
            double credit[] = new double[num];//单科学分
            double score[] = new double[num];//分数
            double gpa[] = new double[num];//单科绩点
            double allCredit = 0;//总学分
            double aveGpa = 0;//平均绩点
            double avgGpa = 0;//最终保留两位小数的平均绩点
            for (int y = 0; y < scList.size(); y++) {
                Score sc = scList.get(y);

                credit[y] = sc.getCourse().getCredit();
                score[y] = sc.getMark();

                if (score[y] < 60) {
                    gpa[y] = 0;
                } else {
                    gpa[y] = (score[y] - 60) / 10 + 1;
                }//及格成绩计入gpa

                allCredit += credit[y];
            }
            for(int j=0;j<scList.size();j++) {
                aveGpa += gpa[j] * credit[j] / allCredit;
                BigDecimal kk = new BigDecimal(aveGpa);
                avgGpa = kk.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            }


            sum=(solAcScore+sciScore+actScore+avgGpa*20)/4;
            BigDecimal gg = new BigDecimal(sum);
            summ = gg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();



            m.put("solAcScore",solAcScore);
            m.put("SciScore",sciScore);
            m.put("actScore",actScore);
            m.put("stuScore",""+avgGpa);
            m.put("Sum",""+summ);
            dataList.add(m);
        }
        return dataList;
    }


    @PostMapping("/sumInit")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse sumInit(@Valid @RequestBody DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List dataList = getSumMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }

    @PostMapping("/sumQuery")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse sumQuery(@Valid @RequestBody DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List dataList = getSumMapList(numName);
        return CommonMethod.getReturnData(dataList); //按照测试框架规范会送Map的list
    }


    @PostMapping("/sumDelete")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse sumDelete(@Valid @RequestBody DataRequest dataRequest) {
        Integer id = dataRequest.getInteger("id");  //获取id值
        Sum s = null;
        Optional<Sum> op;
        if (id != null) {
            op = sumRepository.findById(id);   //查询获得实体对象
            if (op.isPresent()) {
                s = op.get();
            }
        }
        if (s != null) {
            sumRepository.delete(s);    //数据库永久删除
        }
        return CommonMethod.getReturnMessageOK();   //通知前端操作正常
    }





}
