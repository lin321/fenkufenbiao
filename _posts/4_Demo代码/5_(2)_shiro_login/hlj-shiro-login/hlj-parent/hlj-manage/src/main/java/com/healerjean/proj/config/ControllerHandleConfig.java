package com.healerjean.proj.config;

import com.healerjean.proj.dto.ResponseBean;
import com.healerjean.proj.enums.ResponseEnum;
import com.healerjean.proj.exception.AlimamaApiException;
import com.healerjean.proj.exception.BusinessException;
import com.healerjean.proj.exception.HaoDanKuApiException;
import com.healerjean.proj.exception.ParameterErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.UnexpectedTypeException;

/**
 * @author HealerJean
 * @version 1.0v
 * @ClassName ControllerHandleExceptionConfig
 * @date 2019/5/31  20:19.
 * @Description
 */
@Slf4j
@ControllerAdvice
public class ControllerHandleConfig {

    /**
     * 参数非法
     * 1、(BindException : 比如 Integer 传入abc  )
     */
    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class, HttpRequestMethodNotSupportedException.class, HttpMessageConversionException.class, BindException.class, UnexpectedTypeException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseBean httpMessageConversionExceptionHandler(Exception e) {
        log.error("====参数格式异常===", e);
        return ResponseBean.buildFailure(ResponseEnum.参数格式异常.code, e.getMessage());
    }


    /**
     * 参数错误
     */
    @ExceptionHandler(value = ParameterErrorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseBean parameterErrorExceptionHandler(ParameterErrorException e) {
        log.error("参数异常------------参数错误：code：{},message：{}", e.getCode(), e.getMessage());
        return ResponseBean.buildFailure(e.getCode(), e.getMessage());
    }

    /**
     * 淘宝Api接口错误
     */
    @ExceptionHandler(value = {AlimamaApiException.class, HaoDanKuApiException.class})
    @ResponseBody
    public HttpEntity<ResponseBean> taobaoExceptionHandle(BusinessException e) {
        log.error("====淘宝接口错误===", e);
        return returnMessage(ResponseBean.buildFailure(e.getCode(), e.getMessage()));
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(value = BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseBean businessExceptionHandler(HttpServletResponse response, BusinessException e) {
        log.error("业务异常------------异常信息：code：{},message：{}", e.getCode(), e.getMessage());
        return ResponseBean.buildFailure(e.getCode(), e.getMessage());
    }


    /**
     * 所有异常报错
     */
    @ExceptionHandler
    @ResponseBody
    public HttpEntity<ResponseBean> allExceptionHandler(HttpServletResponse response, Exception e) {
        log.error("====系统错误===", e);
        response.setStatus(ResponseEnum.系统错误.code);
        return returnMessage(ResponseBean.buildFailure(ResponseEnum.系统错误));
    }

    private HttpEntity<ResponseBean> returnMessage(ResponseBean responseBean) {
        HttpHeaders header = new HttpHeaders();
        header.add("Content-Type", "application/json");
        header.add("Charset", "UTF-8");
        return new HttpEntity<>(responseBean, header);
    }


}
