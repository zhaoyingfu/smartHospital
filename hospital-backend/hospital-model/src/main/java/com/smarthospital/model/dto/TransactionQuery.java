package com.smarthospital.model.dto;

import lombok.Data;

@Data
public class TransactionQuery extends PageQuery {
    private String bizType;
    private String status;
    private String payMethod;
    private Long kioskId;
}
