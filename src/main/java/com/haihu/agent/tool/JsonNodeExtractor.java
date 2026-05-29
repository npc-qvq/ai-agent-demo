package com.haihu.agent.tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 从工具调用参数 JSON 中提取字段的轻量辅助类。
 */
class JsonNodeExtractor {

    /**
     * JSON 解析器，线程安全。
     */
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * 参数 JSON 的根节点。
     */
    private final JsonNode root;

    /**
     * 解析工具调用参数 JSON。
     *
     * @param json 参数 JSON 字符串
     * @throws RuntimeException 解析失败时抛出
     */
    JsonNodeExtractor(String json) {
        try {
            this.root = mapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException("JSON parse failed: " + json, e);
        }
    }

    /**
     * 从参数 JSON 中按 key 提取字符串值。
     *
     * @param key JSON 字段名
     * @return 字段对应的字符串值，不存在或为 null 时返回 null
     */
    String getString(String key) {
        JsonNode node = root.get(key);
        return node != null && !node.isNull() ? node.asText() : null;
    }
}
