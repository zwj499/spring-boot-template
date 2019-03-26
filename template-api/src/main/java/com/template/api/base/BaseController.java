package com.template.api.base;


import com.baomidou.mybatisplus.plugins.Page;
import com.template.dal.core.bean.ApiBaseResponse;
import com.template.dal.core.bean.PagingResponse;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * @author zhuangwj
 * @since 19-3-26
 */
public class BaseController {
    protected final Logger logger = LogManager.getLogger(this.getClass());

    protected ApiBaseResponse setResponse() {
        return new ApiBaseResponse();
    }

    protected ApiBaseResponse setResponse(Object data) {
        if (data != null) {
            if (data instanceof Page) {
                PagingResponse response = new PagingResponse<>();
                Page<?> page = (Page<?>) data;
                response.setData(page.getRecords());
                response.setTotal(page.getTotal());
                return response;
            } else {
                ApiBaseResponse response = new ApiBaseResponse();
                response.setData(data);
                return response;
            }
        }
        return new ApiBaseResponse();
    }

    protected <T> PagingResponse setResponse(Page<T> page) {
         return new PagingResponse<>(page.getRecords(), page.getTotal());
    }
    protected <T> PagingResponse setResponse(List<T> rows, Integer total) {
        return new PagingResponse<>(rows, total);
    }
}
