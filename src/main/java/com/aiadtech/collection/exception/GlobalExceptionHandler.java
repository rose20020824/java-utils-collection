package com.aiadtech.collection.exception;


import com.aiadtech.collection.constant.ErrorCode;
import com.aiadtech.collection.util.ApiResponse;
import com.aiadtech.collection.util.StringUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 应用异常，通常是一些业务逻辑异常，可直接将错误信息抛给客户端
     * 该异常的逻辑与{@link FatalException}基本一致，区别在于应用异常不会打印日志，详见{@link #fatalException(FatalException)}
     */
    @ExceptionHandler(AppException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse> customException(AppException e) {
        log.info("应用异常:[{}] {}", e.getCode(), e.getMessage());
        return jsonRender(ApiResponse.fail(e.getMessage(), e.getCode()));
    }

    /**
     * 致命异常，通常是一些系统组件抛出的异常，但为了避免内部异常被客户端看到，需要包装成一些通用话术
     * 常见异常例如：
     * {@link java.io.IOException}
     */
    @ExceptionHandler(FatalException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse> fatalException(FatalException e) {
        log.error(e.getErrorCode().getMessage(), e);
        return jsonRender(ApiResponse.fail(e.getErrorCode().getMessage(), e.getErrorCode().getCode()));
    }



    /**
     * 请求参数缺失异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse> missingServletRequestParameterException(MissingServletRequestParameterException e) {
        return jsonRender(ApiResponse.fail(e.getMessage()));
    }


    /**
     * 全局异常，返回HTTP Status 500，在日志中打印对应的异常详情，后续可针对具体的异常编写自定义处理器
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiResponse> throwable(Throwable e) {
        // 生成4位随机串充当uuid，方便查找日志
        var uuid = StringUtil.random(4);
        log.error("捕获未知异常: {}, uuid: {}", e.getClass().getName(), uuid, e);
        var errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        errorCode.updateMessage("内部服务器异常[" + uuid + "]");
        return jsonRender(errorCode, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 请求方法不支持，例如 API只接受POST请求，但实际使用了GET或PUT等非POST请求方式
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse> throwable(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        var errorCode = ErrorCode.METHOD_NOT_ALLOWED;
        log.info("请求方法不支持: {}, uri: {}", e.getMessage(), request.getRequestURI());
        return jsonRender(errorCode, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Json渲染
     */
    private ResponseEntity<ApiResponse> jsonRender(ErrorCode errorCode, HttpStatusCode statusCode) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(ApiResponse.fail(errorCode), headers, statusCode);
    }

    /**
     * Json渲染
     */
    private ResponseEntity<ApiResponse> jsonRender(ApiResponse apiResponse) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(apiResponse, headers, HttpStatus.OK);
    }
}
