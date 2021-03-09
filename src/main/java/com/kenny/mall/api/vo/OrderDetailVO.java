package com.kenny.mall.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class OrderDetailVO implements Serializable {
    private String orderNo;

    private Integer totalPrice;

    private Byte payStatus;

    private Byte payType;

    private String payTypeString;

    private Date payTime;

    private Byte orderStatus;

    private String orderStatusString;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    private List<OrderItemVO>  orderItemVOS;

}
