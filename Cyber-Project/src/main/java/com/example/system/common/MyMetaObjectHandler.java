package com.example.system.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

// Automatically populate some public fields when inserting and updating database records.
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("Public fields are automatically populated [insert]...");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());

        if(BaseContext.getCurrentId()==null){
            metaObject.setValue("updateUser", 1L);
            metaObject.setValue("createUser", 2L);
        }

        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("Public fields are automatically populated [update]....");
        log.info(metaObject.toString());

        metaObject.setValue("updateTime", LocalDateTime.now());

        if(BaseContext.getCurrentId()==null){
            metaObject.setValue("updateUser", 1L);
        }
        metaObject.setValue("updateUser", BaseContext.getCurrentId());

    }
}
