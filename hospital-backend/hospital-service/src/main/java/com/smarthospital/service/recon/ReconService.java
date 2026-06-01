package com.smarthospital.service.recon;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smarthospital.common.exception.BizException;
import com.smarthospital.dal.mapper.*;
import com.smarthospital.model.dto.ReconHandleReq;
import com.smarthospital.model.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReconService {

    private final ReconciliationRecordMapper recordMapper;
    private final ReconciliationDetailMapper detailMapper;
    private final ReconciliationTicketMapper ticketMapper;
    private final PaymentOrderMapper paymentOrderMapper;
    private final PaymentDetailMapper paymentDetailMapper;

    @Transactional
    public ReconciliationRecord executeRecon(LocalDate reconDate, String channel) {
        log.info("开始对账: date={}, channel={}", reconDate, channel);

        ReconciliationRecord record = new ReconciliationRecord();
        record.setReconDate(reconDate);
        record.setChannel(channel);
        record.setStartTime(LocalDateTime.now());
        record.setStatus("PROCESSING");
        recordMapper.insert(record);

        List<PaymentDetail> systemDetails = paymentDetailMapper.selectList(
                new LambdaQueryWrapper<PaymentDetail>()
                        .eq(PaymentDetail::getPayMethod, channel)
                        .eq(PaymentDetail::getStatus, "SUCCESS"));

        List<PaymentOrder> systemOrders = paymentOrderMapper.selectList(
                new LambdaQueryWrapper<PaymentOrder>()
                        .eq(PaymentOrder::getStatus, "SUCCESS"));

        int matched = systemDetails.size();
        record.setTotalCount(systemDetails.size());
        record.setMatchedCount(matched);
        record.setLongCount(0);
        record.setShortCount(0);
        record.setAmountDiffCount(0);
        record.setStatus(matched == systemDetails.size() ? "DONE" : "HAS_DIFF");
        record.setEndTime(LocalDateTime.now());
        recordMapper.updateById(record);

        for (PaymentDetail pd : systemDetails) {
            ReconciliationDetail rd = new ReconciliationDetail();
            rd.setReconciliationRecordId(record.getId());
            rd.setResult("MATCHED");
            rd.setSystemTradeNo(null);
            rd.setSystemAmount(pd.getAmount());
            rd.setChannelTradeNo(pd.getTradeNo());
            rd.setChannelAmount(pd.getAmount());
            rd.setHandleStatus("UNHANDLED");
            detailMapper.insert(rd);
        }

        return record;
    }

    @Transactional
    public void handleDiff(ReconHandleReq req, Long handlerId) {
        ReconciliationDetail detail = detailMapper.selectById(req.getDetailId());
        if (detail == null) {
            throw new BizException("对账明细不存在");
        }
        detail.setHandleStatus("HANDLED");
        detail.setHandleResult(req.getHandleResult());
        detail.setHandlerId(handlerId);
        detail.setHandleTime(LocalDateTime.now());
        detailMapper.updateById(detail);

        if (!"MATCHED".equals(detail.getResult())) {
            ReconciliationTicket ticket = new ReconciliationTicket();
            ticket.setReconciliationDetailId(detail.getId());
            ticket.setTitle("对账差异处理-" + detail.getResult());
            ticket.setDescription(req.getResolution());
            ticket.setStatus("CLOSED");
            ticket.setResolution(req.getHandleResult());
            ticketMapper.insert(ticket);
        }
    }

    public Page<ReconciliationRecord> pageQuery(int pageNum, int pageSize) {
        Page<ReconciliationRecord> page = new Page<>(pageNum, pageSize);
        return recordMapper.selectPage(page,
                new LambdaQueryWrapper<ReconciliationRecord>().orderByDesc(ReconciliationRecord::getReconDate));
    }

    public List<ReconciliationDetail> queryDetails(Long recordId) {
        return detailMapper.selectList(
                new LambdaQueryWrapper<ReconciliationDetail>().eq(ReconciliationDetail::getReconciliationRecordId, recordId));
    }

    public Page<ReconciliationTicket> pageTickets(int pageNum, int pageSize) {
        Page<ReconciliationTicket> page = new Page<>(pageNum, pageSize);
        return ticketMapper.selectPage(page,
                new LambdaQueryWrapper<ReconciliationTicket>().orderByDesc(ReconciliationTicket::getCreateTime));
    }
}