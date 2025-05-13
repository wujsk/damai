package com.damai.dto;


import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: haonan
 * @description: elasticsearch GeoPoint定位
 */
@Data
public class EsGeoPointSortDto {

    /**
     * 字段名称
     */
    private String paramName;

    /**
     * 纬度值
     * */
    private BigDecimal latitude;

    /**
     * 经度值
     * */
    private BigDecimal longitude;
}
