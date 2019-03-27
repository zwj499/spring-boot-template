package com.template.api.base;

import com.baomidou.mybatisplus.plugins.Page;
import com.template.dal.base.BaseModel;
import com.template.dal.core.bean.ApiBaseResponse;
import com.template.dal.core.bean.PagingResponse;
import com.template.dal.core.exception.TemplateCommonExtCodeEnum;
import com.template.dal.core.exception.TemplateException;
import com.template.dal.util.DataUtil;
import com.template.dal.util.StringUtils;
import com.template.domain.base.IBaseService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhuangwj
 * @since 19-3-26
 */
public abstract class AbstractController<T extends IBaseService> extends BaseController {
    protected final Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    protected T service;

    public PagingResponse queryWithPage(Map<String, Object> param) throws TemplateException {
        Map<String, Object> camelCaseMap = new HashMap<>();
        covertUnderscopeMapToCamelCaseMap(param, camelCaseMap);
        Page<?> page = service.queryPage(camelCaseMap);
        PagingResponse response = setResponse(page);
        return response;
    }

    public PagingResponse queryWithPage(BaseModel param, Integer pageNo, Integer pageSize) throws TemplateException {
        Page<?> page = service.queryPageByFieldsOrKeyword(pageNo, pageSize, param);
        PagingResponse response = setResponse(page);
        return response;
    }

    public ApiBaseResponse queryList(Map<String, Object> param) throws TemplateException {
        Map<String, Object> camelToCaseMap = new HashMap<>();
        covertUnderscopeMapToCamelCaseMap(param, camelToCaseMap);
        List list = service.queryList(camelToCaseMap);
        return setResponse(list);
    }

    public ApiBaseResponse queryList(BaseModel param) throws TemplateException {
        List list = service.queryListByFieldsOrKeyword(param);
        return setResponse(list);
    }

    public ApiBaseResponse getDetail(BaseModel param) throws TemplateException {
        if (param.getId() == null) {
            throw new TemplateException(TemplateCommonExtCodeEnum.INVALID_REQUEST_FORMAT);
        }
        BaseModel result = (BaseModel) service.queryById(param.getId());
        return setResponse(result);
    }

    public ApiBaseResponse getDetail(Integer id) throws TemplateException {
        BaseModel result = (BaseModel) service.queryById(id);
        return setResponse(result);
    }

    public ApiBaseResponse updateRecord(BaseModel param) throws TemplateException {
        service.update(param);
        return setResponse();
    }

    public ApiBaseResponse updateRecord(Integer id, BaseModel param) throws TemplateException {
        param.setId(id);
        service.update(param);
        return setResponse();
    }

    public ApiBaseResponse addRecord(BaseModel param) throws TemplateException {
        service.add(param);
        return setResponse();
    }

    public ApiBaseResponse deleteRecord(BaseModel param) throws TemplateException {
        service.logicDel(param);
        return setResponse();
    }

    public ApiBaseResponse deleteRecord(Integer id) throws TemplateException {
        service.logicDel(id);
        return setResponse();
    }


    public void covertUnderscopeMapToCamelCaseMap(Map<String, Object> underscopeMap, Map<String, Object> camelCaseMap){
        if (DataUtil.isEmpty(underscopeMap)) {
            return;
        }
        for (Map.Entry<String, Object> entry : underscopeMap.entrySet()) {
            camelCaseMap.put(StringUtils.camelToCase(entry.getKey()), entry.getValue());
        }
    }

}
