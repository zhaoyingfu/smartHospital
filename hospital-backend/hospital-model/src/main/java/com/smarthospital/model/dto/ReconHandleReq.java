package com.smarthospital.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReconHandleReq {
    @NotNull(message = "明细ID不能为空")
    private Long detailId;
    @NotNull(message = "处理结果不能为空")
    private String handleResult;
    private String resolution;
    private Long handlerId;
}
