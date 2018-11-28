package com.fastjrun.web.controller.advice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    protected final Log log = LogFactory.getLog(this.getClass());

    @ExceptionHandler({RestException.class, ServiceException.class})
    public final ResponseEntity<?> handleException(RestException ex,
                                                   WebRequest request) throws Exception {

        HttpHeaders headers = new HttpHeaders();

        String reqContentType = request.getHeader("Content-Type").replaceAll(
                " ", "");

        headers.setContentType(MediaType.parseMediaType(reqContentType));
        if (reqContentType.indexOf(MediaTypes.JSON) >= 0) {
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

        HttpHeaders headers = new HttpHeaders();

        String reqContentType = request.getHeader("Content-Type").replaceAll(
                " ", "");

        headers.setContentType(MediaType.parseMediaType(reqContentType));
        if (reqContentType.indexOf(MediaTypes.JSON) >= 0) {
            DefaultResponse result = BaseResponseHelper
                    .getFailResult("SYS0001", "系统错误，请稍后重试");

            log.error("异常", ex);

            return handleExceptionInternal(ex, result, headers,
                    HttpStatus.INTERNAL_SERVER_ERROR, request);
        } else {
            return super.handleException(ex, request);
        }

    }

}
