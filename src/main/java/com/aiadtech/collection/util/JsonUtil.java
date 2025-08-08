package com.aiadtech.collection.util;


import com.aiadtech.collection.constant.ErrorCode;
import com.aiadtech.collection.exception.FatalException;
import com.aiadtech.collection.exception.JsonProcessingRuntimeException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class JsonUtil {

    private JsonUtil() {
    }

    private static final JsonMapper jsonMapper = init();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final List<Class<?>> basicsBoxClass = List.of(Integer.class, Long.class, String.class, Boolean.class, Double.class, Float.class);


    private static JsonMapper init() {
        var result = new JsonMapper();
        result.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        result.registerModule(javaTimeModule());

        return result;
    }

    public static JsonMapper getJsonMapper() {
        return jsonMapper;
    }

    private static JavaTimeModule javaTimeModule() {
        var timeModule = new JavaTimeModule();
        timeModule.addSerializer(LocalDateTime.class, new JsonSerializer<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateUtil.NORMAL_DATETIME_FORMAT_STR);

            @Override
            public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(formatter.format(localDateTime));
            }
        });
        timeModule.addDeserializer(LocalDateTime.class, new JsonDeserializer<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateUtil.NORMAL_DATETIME_FORMAT_STR);

            @Override
            public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
                return LocalDateTime.parse(parser.getText(), formatter);
            }
        });

        timeModule.addSerializer(LocalDate.class, new JsonSerializer<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateUtil.NORMAL_DATE_FORMAT_STR);

            @Override
            public void serialize(LocalDate localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeString(formatter.format(localDateTime));
            }
        });
        timeModule.addDeserializer(LocalDate.class, new JsonDeserializer<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateUtil.NORMAL_DATE_FORMAT_STR);

            @Override
            public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
                return LocalDate.parse(parser.getText(), formatter);
            }
        });
        return timeModule;
    }
    public static byte[] serializerAsBytes(Object object) {
        try {
            return jsonMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new JsonProcessingRuntimeException(e);
        }
    }
    /**
     * 判断对象是否为JSON基础类型: null [] int float string bool
     */
    public static boolean isJsonBasicType(Object obj) {
        if (obj.getClass().isArray()) {
            var a = (Object[]) obj;
            // 空数组认为是基础类型
            if (a.length == 0) {
                return true;
            }

            obj = a[0];
        }

        // null也认为是json的基础类型
        return obj == null || obj instanceof Number || obj instanceof String || obj instanceof Boolean;
    }

    public static String serializer(Object object) {
        try {
            return jsonMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.info("Failed to serialize object: {}", object, e);
            throw new JsonProcessingRuntimeException(e);
        }
    }

    public static String serializer(Object object, String errDefaultValue) {
        try {
            return serializer(object);
        } catch (JsonProcessingRuntimeException e) {
            return errDefaultValue;
        }
    }

    public static <T> T deserializer(InputStream stream, TypeReference<T> type) throws IOException {
        return jsonMapper.readValue(stream, type);
    }

    public static <T> T deserializer(InputStream stream, Class<T> type) throws IOException {
        return jsonMapper.readValue(stream, type);
    }

    public static <T> T deserializer(String jsonStr, Class<T> type) throws JsonProcessingException {
        return jsonMapper.readValue(jsonStr, type);
    }

    public static <T> T deserializer(String jsonStr, TypeReference<T> type) throws JsonProcessingException {
        return jsonMapper.readValue(jsonStr, type);
    }

    public static <T> T deserializerByTry(String jsonStr, Class<T> type) {
        try {
            return jsonMapper.readValue(jsonStr, type);
        } catch (JsonProcessingException e) {
            throw new FatalException(ErrorCode.JSON_PROCESSING_EXCEPTION, e);
        }
    }

    public static <T> T deserializer(String jsonStr, Class<T> type, T errDefaultVale) {
        try {
            return deserializer(jsonStr, type);
        } catch (JsonProcessingException e) {
            log.info("Failed to deserialize  jsonStr: {}", jsonStr, e);
            return errDefaultVale;
        }
    }

    public static <T> T deserializer(String jsonStr, TypeReference<T> type, T errDefaultVale) {
        try {
            return deserializer(jsonStr, type);
        } catch (JsonProcessingException e) {
            log.info(" json deserializer error", e);
            return errDefaultVale;
        }
    }

    /**
     * 将json转化成对象
     */
    public static <T> T jsonToObject(Object object, Class<T> source) {
        return jsonMapper.convertValue(object, source);
    }
    public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
        return jsonMapper.convertValue(fromValue, toValueType);
    }
    public static <T> T convertValue(Object fromValue, TypeReference<T> toValueType) {
        return jsonMapper.convertValue(fromValue, toValueType);
    }

    public static <T> List<T> objConversionList(Object obj, Class<T> source) {
        List<T> resultList = new ArrayList<>();
        if (obj instanceof List<?> list && (!list.isEmpty() && list.get(0) instanceof Map)) {
            @SuppressWarnings("unchecked")
            List<Map<String, String>> result = (List<Map<String, String>>) list;
            result.forEach(t -> {
                var entity = JsonUtil.jsonToObject(t, source);
                resultList.add(entity);
            });
            return resultList;
        }
        return new ArrayList<>();
    }

    /**
     * json字符串转成list
     *
     * @return
     */
    public static <T> List<T> jsonToList(String jsonStr, Class<T> clazz) {
        if (jsonStr == null) {
            return new ArrayList<>();
        }
        List<T> list = new ArrayList<>();
        try {
            List<T> datalist = jsonMapper.readValue(jsonStr, new TypeReference<>() {
            });
            datalist = datalist == null ? new ArrayList<>() : datalist;
            datalist.forEach(t -> {
                if (t != null) {
                    var object = JsonUtil.jsonToObject(t, clazz);
                    list.add(object);
                }
            });
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return list;
    }

    /**
     * 对象转换成map
     *
     * @return
     */
    public static <T extends Map> Map objConversionMap(Object obj, Class<T> clazz) {
        if (obj == null || basicsBoxClass.contains(obj.getClass()) || obj instanceof MultipartFile) {
            return null;
        }
        return objectMapper.convertValue(obj, clazz);
    }
}
