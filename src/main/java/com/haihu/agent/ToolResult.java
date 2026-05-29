package com.haihu.agent;

/**
 * 工具调用结果，包含对应的工具调用 ID 和返回内容。
 */
public class ToolResult {

    /**
     * LLM 返回的工具调用 ID，用于关联请求与结果。
     */
    private final String toolCallId;

    /**
     * 工具执行后返回的文本内容。
     */
    private final String content;

    /**
     * 构造工具调用结果。
     *
     * @param toolCallId 工具调用 ID
     * @param content    工具执行返回内容
     */
    public ToolResult(String toolCallId, String content) {
        this.toolCallId = toolCallId;
        this.content = content;
    }

    /**
     * 获取本次工具调用在 LLM 响应中的唯一标识。
     *
     * @return 工具调用 ID
     */
    public String toolCallId() {
        return toolCallId;
    }

    /**
     * 获取工具执行返回的内容。
     *
     * @return 工具返回文本
     */
    public String content() {
        return content;
    }
}
