package com.smarthospital.his.client;

import com.smarthospital.his.dto.*;
import java.util.List;

public interface HisClient {
    PatientDTO queryPatient(String idCard, String medicareCard, String phone);
    PatientDTO createPatient(String name, String idCard, String phone, String medicareCard);
    List<DepartmentDTO> listDepartments();
    List<DoctorDTO> listDoctorsByDepartment(Long departmentId);
    List<ScheduleDTO> listSchedules(Long departmentId, Long doctorId, String date);
    ScheduleDTO getScheduleDetail(Long scheduleId);
    String createRegistration(Long patientId, Long scheduleId, String regType);
    boolean cancelRegistration(String hisOrderNo);
    List<OutpatientOrderDTO> queryOutpatientOrders(Long patientId, String visitNo);
    InpatientInfoDTO queryInpatientInfo(String inpatientNo);
    String createInpatientDeposit(String inpatientNo, Integer amount);
    List<DrugDTO> searchDrugs(String keyword);
    boolean signIn(String hisOrderNo);
    boolean syncPayment(String hisOrderNo, String bizType, Integer amount);
}
