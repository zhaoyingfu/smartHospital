package com.smarthospital.his.mock;

import com.smarthospital.his.client.HisClient;
import com.smarthospital.his.dto.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class HisMockClient implements HisClient {

    private final AtomicLong idSeq = new AtomicLong(1000);

    @Override
    public PatientDTO queryPatient(String idCard, String medicareCard, String phone, String medicalNo) {
        if (idCard == null && medicareCard == null && phone == null && medicalNo == null) {
            return null;
        }
        PatientDTO dto = new PatientDTO();
        dto.setHisPatientId(String.valueOf(idSeq.incrementAndGet()));
        dto.setName("测试患者");
        dto.setIdCard(idCard);
        dto.setPhone(phone);
        dto.setMedicareCard(medicareCard);
        dto.setMedicalNo(medicalNo != null ? medicalNo : "MZ" + System.currentTimeMillis());
        dto.setExists(true);
        return dto;
    }

    @Override
    public PatientDTO createPatient(String name, String idCard, String phone, String medicareCard) {
        PatientDTO dto = new PatientDTO();
        dto.setHisPatientId(String.valueOf(idSeq.incrementAndGet()));
        dto.setName(name);
        dto.setIdCard(idCard);
        dto.setPhone(phone);
        dto.setMedicareCard(medicareCard);
        dto.setMedicalNo("MZ" + System.currentTimeMillis());
        dto.setExists(false);
        return dto;
    }

    @Override
    public List<DepartmentDTO> listDepartments() {
        List<DepartmentDTO> list = new ArrayList<>();
        String[][] depts = {
                {"D001", "内科", "内科"}, {"D002", "外科", "外科"}, {"D003", "妇产科", "妇儿"},
                {"D004", "儿科", "妇儿"}, {"D005", "骨科", "外科"}, {"D006", "眼科", "专科"},
                {"D007", "口腔科", "专科"}, {"D008", "皮肤科", "专科"}, {"D009", "中医科", "中医"},
                {"D010", "神经内科", "内科"}
        };
        for (int i = 0; i < depts.length; i++) {
            DepartmentDTO dto = new DepartmentDTO();
            dto.setHisDeptId(depts[i][0]);
            dto.setName(depts[i][1]);
            dto.setCode(depts[i][0]);
            dto.setCategory(depts[i][2]);
            dto.setSortOrder(i);
            list.add(dto);
        }
        return list;
    }

    @Override
    public List<DoctorDTO> listDoctorsByDepartment(Long departmentId) {
        List<DoctorDTO> list = new ArrayList<>();
        String[] titles = {"主任医师", "副主任医师", "主治医师", "住院医师"};
        String[] names = {"张", "李", "王", "刘", "陈"};
        for (int i = 0; i < 4; i++) {
            DoctorDTO dto = new DoctorDTO();
            dto.setHisDoctorId("DOC" + (departmentId * 10 + i));
            dto.setName(names[i] + "医生");
            dto.setTitle(titles[i]);
            dto.setDepartmentCode("D" + String.format("%03d", departmentId));
            list.add(dto);
        }
        return list;
    }

    @Override
    public List<ScheduleDTO> listSchedules(Long departmentId, Long doctorId, String date) {
        List<ScheduleDTO> list = new ArrayList<>();
        ScheduleDTO dto = new ScheduleDTO();
        dto.setHisScheduleId("SCH" + idSeq.incrementAndGet());
        dto.setHisDoctorId(doctorId != null ? String.valueOf(doctorId) : "DOC1");
        dto.setDepartmentCode("D001");
        dto.setScheduleDate(date != null ? date : LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        dto.setTimePeriod("AM");
        dto.setTotalQuota(30);
        dto.setAvailableQuota(ThreadLocalRandom.current().nextInt(1, 20));
        dto.setRegFee(5000);
        dto.setTreatFee(1000);
        list.add(dto);

        ScheduleDTO dto2 = new ScheduleDTO();
        dto2.setHisScheduleId("SCH" + idSeq.incrementAndGet());
        dto2.setHisDoctorId(doctorId != null ? String.valueOf(doctorId) : "DOC1");
        dto2.setDepartmentCode("D001");
        dto2.setScheduleDate(date != null ? date : LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        dto2.setTimePeriod("PM");
        dto2.setTotalQuota(20);
        dto2.setAvailableQuota(ThreadLocalRandom.current().nextInt(0, 15));
        dto2.setRegFee(5000);
        dto2.setTreatFee(1000);
        list.add(dto2);

        return list;
    }

    @Override
    public ScheduleDTO getScheduleDetail(Long scheduleId) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setHisScheduleId(String.valueOf(scheduleId));
        dto.setHisDoctorId("DOC1");
        dto.setDepartmentCode("D001");
        dto.setScheduleDate(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        dto.setTimePeriod("AM");
        dto.setTotalQuota(30);
        dto.setAvailableQuota(15);
        dto.setRegFee(5000);
        dto.setTreatFee(1000);
        return dto;
    }

    @Override
    public String createRegistration(Long patientId, Long scheduleId, String regType) {
        return "HIS_RG" + System.currentTimeMillis();
    }

    @Override
    public boolean cancelRegistration(String hisOrderNo) {
        return true;
    }

    @Override
    public List<OutpatientOrderDTO> queryOutpatientOrders(Long patientId, String visitNo) {
        List<OutpatientOrderDTO> list = new ArrayList<>();
        OutpatientOrderDTO dto = new OutpatientOrderDTO();
        dto.setHisOrderNo("HIS_OD" + System.currentTimeMillis());
        dto.setVisitNo(visitNo != null ? visitNo : "VZ20260529001");
        dto.setTotalAmount(25600);
        dto.setStatus("UNPAID");

        List<OutpatientItemDTO> items = new ArrayList<>();
        OutpatientItemDTO item1 = new OutpatientItemDTO();
        item1.setItemName("血常规");
        item1.setItemCode("LAB001");
        item1.setQuantity(1);
        item1.setUnitPrice(1500);
        item1.setAmount(1500);
        items.add(item1);

        OutpatientItemDTO item2 = new OutpatientItemDTO();
        item2.setItemName("肝功能检查");
        item2.setItemCode("LAB002");
        item2.setQuantity(1);
        item2.setUnitPrice(8000);
        item2.setAmount(8000);
        items.add(item2);

        OutpatientItemDTO item3 = new OutpatientItemDTO();
        item3.setItemName("CT检查");
        item3.setItemCode("IMG001");
        item3.setQuantity(1);
        item3.setUnitPrice(16100);
        item3.setAmount(16100);
        items.add(item3);

        dto.setItems(items);
        list.add(dto);
        return list;
    }

    @Override
    public InpatientInfoDTO queryInpatientInfo(String inpatientNo) {
        InpatientInfoDTO dto = new InpatientInfoDTO();
        dto.setInpatientNo(inpatientNo);
        dto.setPatientName("测试患者");
        dto.setDepartmentName("内科");
        dto.setBedNo("A-301");
        dto.setBalance(50000);
        return dto;
    }

    @Override
    public String createInpatientDeposit(String inpatientNo, Integer amount) {
        return "HIS_DP" + System.currentTimeMillis();
    }

    @Override
    public List<DrugDTO> searchDrugs(String keyword) {
        List<DrugDTO> list = new ArrayList<>();
        DrugDTO dto = new DrugDTO();
        dto.setHisDrugId("DRG001");
        dto.setName("阿莫西林胶囊");
        dto.setPinYin("AMXLJN");
        dto.setSpec("0.5g*24粒/盒");
        dto.setUnitPrice(1280);
        dto.setCategory("西药");
        list.add(dto);

        DrugDTO dto2 = new DrugDTO();
        dto2.setHisDrugId("DRG002");
        dto2.setName("布洛芬缓释胶囊");
        dto2.setPinYin("BLFHSJN");
        dto2.setSpec("0.3g*20粒/盒");
        dto2.setUnitPrice(1560);
        dto2.setCategory("西药");
        list.add(dto2);
        return list;
    }

    @Override
    public boolean signIn(String hisOrderNo) {
        return true;
    }

    @Override
    public boolean syncPayment(String hisOrderNo, String bizType, Integer amount) {
        return true;
    }
}
