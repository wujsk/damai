package com.damai.mybatisplus;


import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.damai.util.DateUtils;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;

/**
 * @author: haonan
 * @description: 自动填充
 */
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", DateUtils::now, Date.class);
        this.strictInsertFill(metaObject, "editTime", DateUtils::now, Date.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "editTime", DateUtils::now, Date.class);
    }
}
