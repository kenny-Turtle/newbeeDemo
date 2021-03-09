package com.kenny.mall.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class OrderListVO implements Serializable {
    private Long orderId;

    private String roderNo;

    private Byte payType;

    private Byte orderStatus;

    private String orderStatusString;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private List<OrderItemVO> orderItemVOList;

}
