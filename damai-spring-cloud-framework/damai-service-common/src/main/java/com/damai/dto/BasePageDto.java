package com.damai.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author: haonan
 * @description: 基础分页dto
 */
@Data
public class BasePageDto {

    @NotNull
    private Integer pageNumber;


    @NotNull
    private Integer pageSize;
}
