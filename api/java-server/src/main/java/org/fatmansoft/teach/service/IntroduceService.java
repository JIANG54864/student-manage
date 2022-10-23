package org.fatmansoft.teach.service;

import org.fatmansoft.teach.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.repository.ScoreRepository;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.repository.DeptRepository;
import org.fatmansoft.teach.repository.PracticeRepository;
import org.fatmansoft.teach.repository.ActivityRepository;
import org.fatmansoft.teach.repository.SumRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;


@Service
public class IntroduceService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private DeptRepository deptRepository;
    @Autowired
    private PracticeRepository practiceRepository;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private SumRepository sumRepository;

    //个人简历信息数据准备方法  请同学修改这个方法，请根据自己的数据的希望展示的内容拼接成字符串，放在Map对象里， attachList 可以方多段内容，具体内容有个人决定
    public Map getIntroduceDataMap(Integer studentId) {
        Student s = null;
        Map data = new HashMap();
        Optional<Student> op = studentRepository.findById(studentId);
        if (op.isPresent()) {
            s = op.get();
        }

        if (s != null) {

            Dept d;
            d = s.getDept();
            data.put("myName", s.getStudentName());
            data.put("overview", "本人是山东大学" + d.getDeptName() + s.getS_grade() +"级"+ s.getProfession() + "的学生");


            Map m;
            List attachList = new ArrayList();

            Integer stuId = s.getId();
            List<Practice> spList = practiceRepository.findByStudentId1(stuId);
            List<Activity> acList = activityRepository.findByStudentId(stuId);
            StringBuilder solResult = new StringBuilder();
            String solRe = null;
            StringBuilder sciResult = new StringBuilder();
            String sciRe = null;
            StringBuilder acResult = new StringBuilder();
            String acRe = null;
            m = new HashMap<>();
            for(int k = 0;k<spList.size();k++) {
                Practice pr = spList.get(k);
                if((pr.getPractype()).equals("1")||(pr.getPractype()).equals("6")){
                    solResult.append(pr.getPracname()).append("  ");
                    }
            }
            for(int w = 0;w<acList.size();w++){
                Activity ac = acList.get(w);
                acResult.append(ac.getActivityName()).append("  ");}
            solRe = solResult.toString();
            acRe = acResult.toString();
            m.put("title", "社会实践");
            m.put("content",""+solRe);
            attachList.add(m);
            m = new HashMap<>();
            m.put("title", "兴趣爱好");
            m.put("content", s.getHobby());
            attachList.add(m);

            m = new HashMap<>();
            m.put("title", "文体活动");
            m.put("content",acRe);
            attachList.add(m);

            for(int w = 0;w<spList.size();w++) {
                Practice pr1 = spList.get(w);
                m = new HashMap<>();
                if ((pr1.getPractype()).equals("2") || (pr1.getPractype()).equals("3") || (pr1.getPractype()).equals("4") || (pr1.getPractype()).equals("5")) {
                    sciResult.append(pr1.getPracname()).append("  ");
                }
            }
            sciRe = sciResult.toString();
            m.put("title", "学术科研");
            m.put("content",""+sciRe);
            attachList.add(m);


            //Integer stuId = s.getId();
            List<Score> scList = scoreRepository.findByStudentId1(stuId);

            int num = scList.size();
            double credit[] = new double[num];//单科学分
            double score[] = new double[num];//分数
            double gpa[] = new double[num];//单科绩点
            double allCredit = 0;//总学分
            double aveGpa = 0;//平均绩点
            double avgGpa = 0;//最终保留两位小数的平均绩点
            for (int y = 0; y < scList.size(); y++) {
                Score sc = scList.get(y);
                Course c = sc.getCourse();

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

            m = new HashMap<>();
            m.put("title", "学习成绩");
            m.put("content","绩点"+""+avgGpa);
            attachList.add(m);


            List<Sum> suList = sumRepository.findByStudent(stuId);
            Sum sum = suList.get(0);
            Integer sum1 = sum.getSum();
            Integer sum2 = sum.getSciScore();
            Integer sum3 = sum.getSolAcScore();
            Integer sum4 = sum.getActScore();
            StringBuilder sumResult = new StringBuilder();
            String sumRe = null;
            if(sum2>=30){
                sumResult.append("学术达人").append("  ");
                }
            if(sum3>=10){
                sumResult.append("实践达人").append("  ");
            }
            if(sum4>=20){
                sumResult.append("文体达人").append("  ");
            }
            if(sum1>=35){
                sumResult.append("全面发展，优秀同学").append("  ");
            }
            sumRe = sumResult.toString();
            m = new HashMap();
            m.put("title","个人评价");
            m.put("content",""+sumRe);
            attachList.add(m);




            data.put("attachList",attachList);
        }

        return data;


    }
}










//个人画像的综合评价：根据学生的各种表现数据计算一个分值，在画像中有所体现