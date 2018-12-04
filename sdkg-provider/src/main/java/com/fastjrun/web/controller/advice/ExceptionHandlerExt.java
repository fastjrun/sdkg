package com.fastjrun.web.controller.advice;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fastjrun.common.MediaTypes;
import com.fastjrun.common.RestException;
import com.fastjrun.common.ServiceException;
import com.fastjrun.dto.DefaultResponse;
import com.fastjrun.helper.BaseResponseHelper;

@ControllerAdvice
public class ExceptionHandlerExt extends ResponseEntityExceptionHandler {

    public static final String CODE_SYSTEM_ERROR = "9999";

    public static final String MSG_SYSTEM_ERROR = "System Error";

    @Resource
    protected MessageSource serviceMessageSource;

    protected final Log log = LogFactory.getLog(this.getClass());

    private String parseReqContentType(@NotNull WebRequest request) {

        String reqContentType = request.getHeader("Content-Type");
        if (reqContentType != null) {
            reqContentType = reqContentType.replaceAll(" ", "");
        } else {
            reqContentType = MediaTypes.JSON;
        }
        return reqContentType;
    }

    @ExceptionHandler({RestException.class, ServiceException.class})
    public final ResponseEntity<?> handleException(RestException ex,
                                                   WebRequest request) throws Exception {

        log.error("异常", ex);
        String reqContentType = this.parseReqContentType(request);
        if (reqContentType.indexOf(MediaTypes.JSON) >= 0) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(reqContentType));
            DefaultResponse result = BaseResponseHelper
                    .getFailResult(ex);
            log.error("异常", ex);
            return handleExceptionInternal(ex, result, headers, HttpStatus.OK,
                    request);
        } else {
            return super.handleException(ex, request);
        }

    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<?> handleSysException(Exception ex,
                                                      WebRequest request) throws Exception {

        log.error("异常", ex);
        String reqContentType = this.parseReqContentType(request);
        if (reqContentType.indexOf(MediaTypes.JSON) >= 0) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(reqContentType));
            DefaultResponse result = BaseResponseHelper.getFailResult(CODE_SYSTEM_ERROR,
                    serviceMessageSource.getMessage(CODE_SYSTEM_ERROR, null, MSG_SYSTEM_ERROR, null));

            return handleExceptionInternal(ex, result, headers,
                    HttpStatus.INTERNAL_SERVER_ERROR, request);
        } else {
            return super.handleException(ex, request);
        }

    }

}
