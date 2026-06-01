package com.smarthospital.web.controller.kiosk;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smarthospital.common.response.R;
import com.smarthospital.dal.mapper.DepartmentMapper;
import com.smarthospital.dal.mapper.DoctorMapper;
import com.smarthospital.dal.mapper.ScheduleMapper;
import com.smarthospital.model.dto.RegistrationReq;
import com.smarthospital.model.entity.Department;
import com.smarthospital.model.entity.Doctor;
import com.smarthospital.model.entity.Registration;
import com.smarthospital.model.entity.Schedule;
import com.smarthospital.service.registration.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/kiosk/registration")
@RequiredArgsConstructor
public class KioskRegistrationController {

    private final RegistrationService registrationService;
    private final DepartmentMapper departmentMapper;
    private final DoctorMapper doctorMapper;
    private final ScheduleMapper scheduleMapper;

    @GetMapping("/departments")
    public R<List<Department>> listDepartments() {
        return R.ok(departmentMapper.selectList(
                new LambdaQueryWrapper<Department>().eq(Department::getStatus, 1).orderByAsc(Department::getSortOrder)));
    }

    @GetMapping("/doctors")
    public R<List<Doctor>> listDoctors(@RequestParam Long departmentId) {
        return R.ok(doctorMapper.selectList(
                new LambdaQueryWrapper<Doctor>().eq(Doctor::getDepartmentId, departmentId).eq(Doctor::getStatus, 1)));
    }

    @GetMapping("/schedules")
    public R<List<Schedule>> listSchedules(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<Schedule>();
        if (departmentId != null) wrapper.eq(Schedule::getDepartmentId, departmentId);
        if (doctorId != null) wrapper.eq(Schedule::getDoctorId, doctorId);
        if (date != null) wrapper.eq(Schedule::getScheduleDate, date);
        else wrapper.ge(Schedule::getScheduleDate, LocalDate.now());
        wrapper.ne(Schedule::getStatus, "DISABLED");
        return R.ok(scheduleMapper.selectList(wrapper));
    }

    @PostMapping("/register")
    public R<Registration> register(@Valid @RequestBody RegistrationReq req) {
        return R.ok(registrationService.register(req));
    }

    @GetMapping("/records")
    public R<List<Registration>> queryRecords(@RequestParam Long patientId) {
        return R.ok(registrationService.queryByPatientId(patientId));
    }
}