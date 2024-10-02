package com.xstocks.referral.utils;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class JsonUtil {


    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将对象转json
     *
     * @param object
     * @return
     */
    public static String toJSONString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("toJSONString object：{}", object, e);
        }
        return null;
    }

    public static ObjectNode toObjectNode(Object object) {
        try {
            return (ObjectNode) objectMapper.readTree(toJSONString(object));
        } catch (Exception e) {
            log.error("toObjectNode error", e);
        }
        return null;
    }

    /**
     * json 字符串转对象
     *
     * @param jsonString
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T parseObject(String jsonString, Class<T> clazz) {
        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (Exception e) {
            log.error("parseObject exception jsonString is {}", jsonString, e);
        }
        return null;
    }

    public static <T> T jsonNode2Object(JsonNode jsonNode, Class<T> clazz) {
        try {
            return objectMapper.treeToValue(jsonNode, clazz);
        } catch (JsonProcessingException e) {
            log.error("jsonNode2Object exception", e);
        }
        return null;
    }

    public static <T> T jsonNode2Object(JsonNode jsonNode, TypeReference<T> jsonTypeReference) {
        try {
            ObjectReader reader = objectMapper.readerFor(jsonTypeReference);
            return reader.readValue(jsonNode);
        } catch (IOException e) {
            log.error("jsonNode2Object exception", e);
        }
        return null;
    }

    /**
     * 将json array反序列化为对象
     *
     * @param jsonTypeReference
     * @return
     */
    public static <T> T parseObject(String jsonString, TypeReference<T> jsonTypeReference) {
        try {
            return (T) objectMapper.readValue(jsonString, jsonTypeReference);
        } catch (Exception e) {
            log.error("parseObject exception jsonString is {}", jsonString, e);
        }
        return null;
    }

    public static Map<Object, Object> convertPojo2Map(Object pojo) {
        try {
            return objectMapper.convertValue(pojo, new TypeReference<Map<Object, Object>>() {
            });
        } catch (Exception e) {
            log.error("convertPojo2Map exception ", e);
        }
        return null;
    }

    /**
     * json 字符串转对象
     *
     * @param jsonString
     * @return
     * @throws Exception
     */
    public static Map parseObject(String jsonString) {
        try {
            return parseObject(jsonString, Map.class);
        } catch (Exception e) {
            log.error("parseObject jsonString ：{}", jsonString, e);
        }
        return null;
    }

    public static <T> T parseObject(String str, Class<?> collectionClazz, Class<?>... elermentclazzes) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClazz, elermentclazzes);
        try {
            return objectMapper.readValue(str, javaType);
        } catch (IOException e) {
            log.error(" parseObject error : {}", str, e);
            return null;
        }
    }

    /**
     * 将jsonString 中某个key 对应的值转成
     *
     * @param map
     * @return
     * @throws Exception
     */
    public static List<Map> getJSONArray(Map map, String key) {
        try {
            return parseObject((String) map.get(key), List.class, Map.class);
        } catch (Exception e) {
            log.error("parseObject", e);
        }
        return null;
    }

    /**
     * 将jsonString 转成List<Object>
     *
     * @param jsonString
     * @return
     * @throws Exception
     */
    public static List<Map> getJSONArray(String jsonString) {
        try {
            return parseObject(jsonString, List.class, Map.class);
        } catch (Exception e) {
            log.error("parseObject", e);
        }
        return null;
    }

    /**
     * 将jsonString 转成List<Object>
     *
     * @param jsonString
     * @return
     * @throws Exception
     */
    public static <T> List<T> getJSONArray(String jsonString, Class<T> clazz) {
        try {
            return parseObject(jsonString, List.class, clazz);
        } catch (Exception e) {
            log.error("parseObject", e);
        }
        return null;
    }

    public static <T> List<T> convertStringListToObjectList(List<String> list, Class<T> clazz) {
        try {
            return list.stream()
                    .map(json -> JSON.parseObject(json,clazz)).collect(Collectors.toList());
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return null;
        }
    }

    public static ObjectNode createObjectNode() {
        return objectMapper.createObjectNode();
    }

    public static JsonNode toJsonNode(Object o) {
        return objectMapper.valueToTree(o);
    }
}
