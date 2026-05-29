package com.haihu.agent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 与 OpenAI 兼容 Chat Completions API 通信的 LLM 客户端。
 * <p>
 * 向 /v1/chat/completions 发送请求，支持工具定义透传和 tool_calls 响应解析。
 */
public class LLMClient {

    /**
     * LLM API 密钥。
     */
    private final String apiKey;

    /**
     * API 基础地址，例如 https://api.deepseek.com。
     */
    private final String baseUrl;

    /**
     * 调用的大模型名称。
     */
    private final String model;

    /**
     * JSON 序列化/反序列化器。
     */
    private final ObjectMapper mapper;

    /**
     * HTTP 客户端，连接超时 30 秒。
     */
    private final HttpClient httpClient;

    /**
     * 构建 LLM 客户端。
     *
     * @param apiKey  API 密钥
     * @param baseUrl API 基础地址
     * @param model   模型名称
     */
    public LLMClient(String apiKey, String baseUrl, String model) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.model = model;
        this.mapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    /**
     * 发送多轮对话请求，可附带工具定义。
     *
     * @param messages 对话历史消息列表
     * @param tools    可用工具列表，为空时不传 tools 字段
     * @return LLM 响应（包含文本内容或工具调用列表）
     * @throws IOException          HTTP 通信或 JSON 序列化异常
     * @throws InterruptedException 请求被中断
     */
    public LLMResponse chat(List<Message> messages, List<Tool> tools) throws IOException, InterruptedException {
        Map<String, Object> body = new java.util.LinkedHashMap<>();
        body.put("model", model);
        body.put("messages", messages);

        if (tools != null && !tools.isEmpty()) {
            List<Map<String, Object>> toolDefs = new ArrayList<>();
            for (Tool t : tools) {
                Map<String, Object> def = new java.util.LinkedHashMap<>();
                def.put("type", "function");

                Map<String, Object> function = new java.util.LinkedHashMap<>();
                function.put("name", t.name());
                function.put("description", t.description());
                function.put("parameters", t.parameters());

                def.put("function", function);
                toolDefs.add(def);
            }
            body.put("tools", toolDefs);
        }

        String json = mapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .timeout(Duration.ofSeconds(120))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("API error " + response.statusCode() + ": " + response.body());
        }

        return parseResponse(response.body());
    }

    /**
     * 解析 Chat Completions API 响应体为 LLMResponse 对象。
     *
     * @param body API 响应 JSON 字符串
     * @return 解析后的 LLMResponse
     * @throws JsonProcessingException JSON 解析失败
     */
    private LLMResponse parseResponse(String body) throws JsonProcessingException {
        JsonNode root = mapper.readTree(body);
        JsonNode choice = root.get("choices").get(0);
        JsonNode message = choice.get("message");

        String content = message.has("content") && !message.get("content").isNull()
                ? message.get("content").asText()
                : null;

        List<ToolCall> toolCalls = null;
        if (message.has("tool_calls") && !message.get("tool_calls").isNull()) {
            toolCalls = new ArrayList<>();
            for (JsonNode toolCall : message.get("tool_calls")) {
                toolCalls.add(mapper.treeToValue(toolCall, ToolCall.class));
            }
        }

        String finishReason = choice.has("finish_reason") && !choice.get("finish_reason").isNull()
                ? choice.get("finish_reason").asText()
                : "stop";

        return new LLMResponse(content, toolCalls, finishReason);
    }

    /**
     * LLM 返回的响应对象，包含文本内容、工具调用列表和结束原因。
     */
    public static class LLMResponse {

        /**
         * LLM 返回的文本内容，触发工具调用时为空。
         */
        private final String content;

        /**
         * LLM 请求的工具调用列表，无工具调用时为空。
         */
        private final List<ToolCall> toolCalls;

        /**
         * 响应结束原因：stop / tool_calls / length 等。
         */
        private final String finishReason;

        /**
         * 构造 LLM 响应。
         *
         * @param content      文本内容
         * @param toolCalls    工具调用列表
         * @param finishReason 结束原因
         */
        public LLMResponse(String content, List<ToolCall> toolCalls, String finishReason) {
            this.content = content;
            this.toolCalls = toolCalls;
            this.finishReason = finishReason;
        }

        /**
         * 获取 LLM 返回的文本内容。
         *
         * @return 文本内容，可能为 null
         */
        public String getContent() {
            return content;
        }

        /**
         * 获取 LLM 请求的工具调用列表。
         *
         * @return 工具调用列表，可能为 null
         */
        public List<ToolCall> getToolCalls() {
            return toolCalls;
        }

        /**
         * 获取响应结束原因。
         *
         * @return 结束原因字符串
         */
        public String getFinishReason() {
            return finishReason;
        }

        /**
         * 判断 LLM 是否请求了工具调用。
         *
         * @return 存在工具调用时返回 true
         */
        public boolean hasToolCalls() {
            return toolCalls != null && !toolCalls.isEmpty();
        }
    }
}
