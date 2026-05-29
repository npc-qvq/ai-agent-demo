package com.haihu.agent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * OpenAI 兼容的消息对象，支持 system、user、assistant、tool 四种角色。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message {

    /**
     * 消息角色：system / user / assistant / tool。
     */
    private String role;

    /**
     * 消息文本内容，tool_calls 存在时可为空。
     */
    private String content;

    /**
     * LLM 请求的工具调用列表，仅 assistant 角色使用。
     */
    private List<ToolCall> tool_calls;

    /**
     * 工具调用 ID，仅 tool 角色使用，用于关联 assistant 的 tool_calls。
     */
    private String tool_call_id;

    /**
     * 工具名称，仅 tool 角色使用。
     */
    private String name;

    /**
     * 创建 system 角色消息。
     *
     * @param content 系统提示词内容
     * @return system 消息
     */
    public static Message system(String content) {
        Message message = new Message();
        message.role = "system";
        message.content = content;
        return message;
    }

    /**
     * 创建 user 角色消息。
     *
     * @param content 用户输入内容
     * @return user 消息
     */
    public static Message user(String content) {
        Message message = new Message();
        message.role = "user";
        message.content = content;
        return message;
    }

    /**
     * 创建 assistant 角色消息。
     *
     * @param content LLM 回复文本
     * @return assistant 消息
     */
    public static Message assistant(String content) {
        Message message = new Message();
        message.role = "assistant";
        message.content = content;
        return message;
    }

    /**
     * 创建携带工具调用指令的 assistant 消息。
     *
     * @param toolCalls LLM 请求的工具调用列表
     * @return assistant 消息（含 tool_calls，不含 content）
     */
    public static Message assistantWithToolCalls(List<ToolCall> toolCalls) {
        Message message = new Message();
        message.role = "assistant";
        message.tool_calls = toolCalls;
        return message;
    }

    /**
     * 创建 tool 角色消息，携带工具执行结果。
     *
     * @param toolCallId 对应的工具调用 ID
     * @param name       工具名称
     * @param content    工具执行返回内容
     * @return tool 消息
     */
    public static Message tool(String toolCallId, String name, String content) {
        Message message = new Message();
        message.role = "tool";
        message.tool_call_id = toolCallId;
        message.name = name;
        message.content = content;
        return message;
    }

    public String role() {
        return role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String content() {
        return content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<ToolCall> tool_calls() {
        return tool_calls;
    }

    public List<ToolCall> getTool_calls() {
        return tool_calls;
    }

    public void setTool_calls(List<ToolCall> tool_calls) {
        this.tool_calls = tool_calls;
    }

    public String tool_call_id() {
        return tool_call_id;
    }

    public String getTool_call_id() {
        return tool_call_id;
    }

    public void setTool_call_id(String tool_call_id) {
        this.tool_call_id = tool_call_id;
    }

    public String name() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
