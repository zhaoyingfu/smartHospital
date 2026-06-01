package com.smarthospital.service.kiosk;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smarthospital.dal.mapper.KioskHeartbeatMapper;
import com.smarthospital.dal.mapper.KioskMachineMapper;
import com.smarthospital.model.entity.KioskHeartbeat;
import com.smarthospital.model.entity.KioskMachine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KioskMonitorService {

    private final KioskMachineMapper kioskMachineMapper;
    private final KioskHeartbeatMapper heartbeatMapper;

    public KioskMachine findByMachineNo(String machineNo) {
        return kioskMachineMapper.selectOne(
                new LambdaQueryWrapper<KioskMachine>().eq(KioskMachine::getMachineNo, machineNo));
    }

    public KioskMachine findById(Long id) {
        return kioskMachineMapper.selectById(id);
    }

    public void heartbeat(Long kioskId, Integer paperRemaining) {
        KioskMachine machine = kioskMachineMapper.selectById(kioskId);
        if (machine != null) {
            machine.setStatus("ONLINE");
            machine.setLastHeartbeat(LocalDateTime.now());
            if (paperRemaining != null) {
                if (paperRemaining <= 0) {
                    machine.setPaperStatus("EMPTY");
                } else if (paperRemaining <= 20) {
                    machine.setPaperStatus("LOW");
                } else {
                    machine.setPaperStatus("NORMAL");
                }
            }
            kioskMachineMapper.updateById(machine);
        }

        KioskHeartbeat hb = new KioskHeartbeat();
        hb.setKioskId(kioskId);
        hb.setHeartbeatTime(LocalDateTime.now());
        hb.setPaperRemaining(paperRemaining);
        heartbeatMapper.insert(hb);
    }

    public List<KioskMachine> listAll() {
        return kioskMachineMapper.selectList(null);
    }

    public Page<KioskMachine> pageQuery(int pageNum, int pageSize) {
        Page<KioskMachine> page = new Page<>(pageNum, pageSize);
        return kioskMachineMapper.selectPage(page, null);
    }

    public void updateFeatures(Long kioskId, String features) {
        KioskMachine machine = kioskMachineMapper.selectById(kioskId);
        if (machine != null) {
            machine.setFeatures(features);
            kioskMachineMapper.updateById(machine);
        }
    }

    public void detectOffline() {
        List<KioskMachine> machines = kioskMachineMapper.selectList(
                new LambdaQueryWrapper<KioskMachine>().eq(KioskMachine::getStatus, "ONLINE"));
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(3);
        for (KioskMachine m : machines) {
            if (m.getLastHeartbeat() != null && m.getLastHeartbeat().isBefore(threshold)) {
                m.setStatus("OFFLINE");
                kioskMachineMapper.updateById(m);
            }
        }
    }
}