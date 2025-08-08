package com.aiadtech.collection.constant;

public enum ErrorCode {

    OK(0, "success"),
    ILLEGALITY_REQUEST(100, "非法请求"),
    // jsapi
    CREATE_TOKEN_FAIL(200001, "校验失败，请检查appId与secret是否正确"),
    CREATE_TOKEN_EXCEED_LIMIT(200002, "创建Token次数达到上限，请0点后重试"),
    JSAPI_WS_UNAUTHORIZED(200003, "Ticket not found"),
    JSAPI_WS_FORBIDDEN(200004, "Agent ID not match"),
    JSAPI_WS_UNPROCESSABLE_ENTITY(200005, null),
    JSAPI_CALL_LIST_FORM_ERROR(200006, "时间范围错误"),
    JSAPI_CALL_LIST_FORM_DATE_NULL(200007, "时间范围不允许为空"),
    JSAPI_DATA_NOT_FOUND(200008, "数据不存在"),
    JSAPI_UPDATE_SUMMARY_ERROR(200009, "编辑通话总结失败，通话总结执行中"),

    // openapi
    UNKNOWN_EVENT(300001, "无法识别的消息类型"),
    OPENAPI_WS_UNPROCESSABLE_ENTITY(300002, null),
    OPENAPI_WS_UNAUTHORIZED(300003, "Token not found"),
    OPENAPI_TEXT_SUMMARY_PARAMS_ERROR(300004, "文本小结入参错误"),
    OPENAPI_UIE_REQUEST_ERROR(300005, "实体抽取请求失败"),
    OPENAPI_CALLBACK_URL_ERROR(300006, "数据回流地址错误"),
    OPENAPI_TEXT_SUMMARY_TEXT_LENGTH_ERROR(300007, "文本小结文本长度超出限制"),
    OPENAPI_USER_LABEL_PARAMS_ERROR(300008, "用户标签入参错误"),

    // portal
    LOGIN_FAIL(400001, "用户名或密码错误"),
    PORTAL_FORM_TIMEOUT(400002, "时间范围不允许超过365天"),
    PORTAL_FORM_TIME_RANGE_ERROR(400003, "时间范围错误"),
    PORTAL_ES_FAIL(400004, "ES查询失败"),
    PORTAL_EXPORTING(400005, "导出失败：有导出任务执行中"),
    PORTAL_EXPORT_RECORD_NOT_EXIST(400006, "导出记录不存在"),
    PORTAL_EXPORT_STATUS_ERROR(400007, "导出任务已完成或已失效"),
    PORTAL_PROMPT_NAME_EXIST(400008, "指令模板名称已存在"),
    PORTAL_PROMPT_NOT_EXIST(400009, "指令模板不存在"),
    PORTAL_PROMPT_NOT_ALLOW_EDIT(400010, "该指令模板不允许操作"),
    ES_SEARCH_MAX_COUNT_ERROR(400011, "ES查询数据超出上限"),
    PORTAL_KNOWLEDGE_BASE_NOT_EXIST(400012, "知识库不存在"),
    PORTAL_KNOWLEDGE_BASE_NAME_EXIST(400013, "知识库名称已存在"),
    PORTAL_KNOWLEDGE_CLASSIFICATION_NOT_EXIST(400014, "知识分类不存在"),
    PORTAL_DOC_SIZE_LIMIT(400015, "文件大小超出限制"),
    PORTAL_DOC_TYPE_ERROR(400016, "文件类型错误"),
    PORTAL_KNOWLEDGE_CLASSIFICATION_LEVEL_ERROR(400017, "知识分类层级超出限制"),
    PORTAL_KNOWLEDGE_CLASSIFICATION_COUNT_MAX(400018, "知识分类数量超出限制"),
    PORTAL_KNOWLEDGE_CLASSIFICATION_TITLE_EXIST(400019, "知识分类名称已存在"),
    PORTAL_KNOWLEDGE_BASE_COUNT_MAX(400020, "知识库数量超出上限"),
    PORTAL_KNOWLEDGE_CLASSIFICATION_DELETE_FAIL(400021, "该分类或其下级分类 已包含文件或问答，无法删除"),
    PORTAL_QA_LIBRARY_NOT_EXIST(400022, "问答库不存在"),
    PORTAL_KNOWLEDGE_BASE_COPY_ERROR(400023, "知识库复制失败"),
    PORTAL_KNOWLEDGE_BASE_CAN_NOT_COPY(400024, "知识库复制失败，存在处理中文件"),
    PORTAL_QA_LIBRARY_IS_EXIST(400025, "该分类下问题已存在"),

    // llm
    SPARK_SIGNATURE_ERROR(500001, "星火大模型签名失败"),
    SPARK_DIALOGUE_ERROR(500002, "星火大模型交互失败"),
    PROMPT_VARIABLES_ERROR(500003, "变量匹配错误"),

    // langchain
    LANG_CHAIN_LOAD_DOCUMENT_ERROR(600001, "文件加载失败"),

    // toolkit
    FILE_GET_ERROR(700001, "获取文件解析失败"),
    TOKEN_GET_ERROR(700002, "token获取失败"),
    RSA_PROCESSING_EXCEPTION(700003, "RSA加密异常"),
    AES_DECRYPT_EXCEPTION(700004, "AES解密异常"),
    COURSE_IS_EXIST(700005, "课程信息已存在"),
    COURSE_IS_NOT_EXIST(700006, "课程信息不存在"),
    PERMISSION_DENIED(106, "权限不足"),
    LOGIN_STATUS_EXPIRED(102, "请重新登录"),
    RABBITMQ_BINDING_NAME_EXCEPTION(104, "Rabbitmq 队列绑定错误，可能存在空交换机名或空队列名"),
    RABBITMQ_BINDING_UNKNOW_EXCEPTION(105, "Rabbitmq 队列绑定错误"),
    UNKNOWN_ERROR(101, "未知错误"),
    CLIENT_ERROR(107, "client校验错误"),

    // gateway
    GATEWAY_USER_ERROR(108, "网关用户信息解析失败"),


    // 4xx 5xx的code为预留值，与HTTP Status一致，请勿随意占用
    //Token验证失败
    UNAUTHORIZED(403, "Invalid or expired token"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    INTERNAL_SERVER_ERROR(500, "内部服务器异常"),

    JSON_PROCESSING_EXCEPTION(1000, "Json处理异常"),
    DATE_PROCESSING_EXCEPTION(1001, "日期格式处理异常"),

    // common
    COMPANY_NO_EXIST(10000, "公司不存在"),
    TOKEN_NOT_FOUND(100001, "Token已失效"),
    TICKET_NOT_FOUND(100002, "会话已过期，请重新登录"),
    FORM_VALID_FAIL(100003, "表单校验失败，请重写message"),
    URL_PARSE_FAIL(100004, "url地址解释错误"),

    ENCRYPT_FAIL(200009, "AES加密失败"),
    VALIDATOR_ERROR(200010, "校验数据不通过"),

    //baidu
    TIMEOUT(52001, "请求超时"),
    SYSTEM_ERROR(52002, "系统错误"),
    MISSING_PARAM(54000, "必填参数为空"),
    SIGNATURE_ERROR(54001, "签名错误"),
    RATE_LIMITED(54003, "访问频率受限"),
    INSUFFICIENT_BALANCE(54004, "账户余额不足"),
    LONG_QUERY_FREQUENCY(54005, "长query请求频繁"),
    INVALID_IP(58000, "客户端IP非法"),
    UNSUPPORTED_LANGUAGE(58001, "译文语言方向不支持"),
    SERVICE_DISABLED(58002, "服务当前已关闭"),
    IP_BLOCKED(58003, "此IP已被封禁"),
    AUTH_FAILED(90107, "认证未通过或未生效"),
    SECURITY_RISK(20003, "请求内容存在安全风险"),

    //公告
    LANGUAGE_MISSING(20001, "语言缺失配置"),
    LANGUAGE_EXCEED_QUANTITY(20002, "公告数量超过10条限制"),
    LANGUAGE_NOT_COUNT(20022, "语言代码存在无效内容或数量不匹配"),

   USER_NOT_FOUND(109, "用户信息不存在"),
    //用户来源不正确
    USER_SOURCE_ERROR(110, "用户来源不正确"),
    ;

    private final int code;

    private String message;

    ErrorCode(int code, String message) {
        this.message = message;
        this.code = code;
    }

    public ErrorCode updateMessage(String message) {
        this.message = message;
        return this;
    }
    public static String getMsg(int code) {
        for (ErrorCode error : values()) {
            if (error.code==code) {
                return error.getMessage();
            }
        }
        return "系统错误";
    }
    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

}
