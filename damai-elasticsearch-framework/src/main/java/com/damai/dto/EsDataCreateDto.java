package com.damai.dto;


import lombok.Data;

/**
 * @author: haonan
 * @description: elasticsearch数据参数
 */
@Data
public class EsDataCreateDto {

    /**
     * 参数名称
     */
    private String paramName;

    /**
     * 参数值
     */
    private String paramValue;
}
