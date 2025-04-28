package com.damai.page;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author: haonan
 * @description: 分页vo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageVo<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public long currentPage = 1;

    public long pageSize = 10;

    public long total;

    public List<T> data;
}
