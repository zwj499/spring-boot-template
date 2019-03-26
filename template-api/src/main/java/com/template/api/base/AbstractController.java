package com.template.api.base;

import com.template.dal.core.bean.PagingResponse;
import com.template.dal.core.exception.TemplateException;
import com.template.domain.base.IBaseService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @author zhuangwj
 * @since 19-3-26
 */
public abstract class AbstractController<T extends IBaseService> extends BaseController {
    protected final Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    protected T service;

}
