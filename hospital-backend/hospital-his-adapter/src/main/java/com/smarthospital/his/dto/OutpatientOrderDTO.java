package com.smarthospital.his.dto;

import lombok.Data;
import java.util.List;

@Data
public class OutpatientOrderDTO {
    private String hisOrderNo;
    private String visitNo;
    private Integer totalAmount;
    private String status;
    private List<OutpatientItemDTO> items;
}
