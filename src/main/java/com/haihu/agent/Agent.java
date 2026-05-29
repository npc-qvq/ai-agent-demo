package com.haihu.agent;

import java.util.ArrayList;
import java.util.List;

/**
 * Agent 核心循环控制器。
 * <p>
 * 编排"用户输入 → LLM 推理 → 工具调用 → 工具结果 → LLM 最终回复"的完整流程，
 * 在不超过最大迭代次数的情况下持续调用 LLM 直到获得文本回复。
 */
public class Agent {

    /**
     * 用于与 LLM API 通信的客户端。
     */
    private final LLMClient llmClient;

    /**
     * Agent 可用的工具列表。
     */
    private final List<Tool> tools;

    /**
     * 系统提示词，定义 Agent 的行为和角色。
     */
    private final String systemPrompt;

    /**
     * 最大迭代次数，防止无限循环调用工具。
     */
    private final int maxIterations;

    /**
     * 构造 Agent 实例。
     *
     * @param llmClient     LLM 通信客户端
     * @param tools         可用工具列表
     * @param systemPrompt  系统提示词
     * @param maxIterations 最大迭代次数
     */
    public Agent(LLMClient llmClient, List<Tool> tools, String systemPrompt, int maxIterations) {
        this.llmClient = llmClient;
        this.tools = tools;
        this.systemPrompt = systemPrompt;
        this.maxIterations = maxIterations;
    }

    /**
     * 执行一次完整的 Agent 对话。
     * <p>
     * 将用户消息提交给 LLM，若 LLM 返回工具调用则在本地执行工具并将结果回传，
     * 循环直至 LLM 返回文本回复或达到最大迭代次数。
     *
     * @param userMessage 用户输入的文本
     * @return LLM 最终回复或错误提示
     */
    public String run(String userMessage) {
        List<Message> messages = new ArrayList<>();
        if (systemPrompt != null && !systemPrompt.isBlank()) {
            messages.add(Message.system(systemPrompt));
        }
        messages.add(Message.user(userMessage));

        int iteration = 0;
        while (iteration < maxIterations) {
            iteration++;
            System.out.println("[Agent] Iteration " + iteration + "...");

            LLMClient.LLMResponse response;
            try {
                response = llmClient.chat(messages, tools);
            } catch (Exception e) {
                return "调用 LLM 失败: " + e.getMessage();
            }

            if (response.hasToolCalls()) {
                System.out.println("[Agent] LLM requested " + response.getToolCalls().size() + " tool call(s)");

                messages.add(Message.assistantWithToolCalls(response.getToolCalls()));

                for (ToolCall tc : response.getToolCalls()) {
                    String toolName = tc.function().name();
                    String args = tc.function().arguments();
                    System.out.println("[Agent]   Calling tool: " + toolName + "(" + args + ")");

                    String result = executeTool(toolName, args);
                    System.out.println("[Agent]   Tool result: " + truncate(result, 200));

                    messages.add(Message.tool(tc.id(), toolName, result));
                }
            } else {
                return response.getContent() != null ? response.getContent() : "(LLM 未返回内容)";
            }
        }
        return "(达到最大迭代次数 " + maxIterations + ")";
    }

    /**
     * 按名称查找并执行本地工具。
     *
     * @param name 工具名称
     * @param args JSON 格式的工具调用参数
     * @return 工具执行结果，或错误描述（工具未找到 / 执行异常）
     */
    private String executeTool(String name, String args) {
        for (Tool t : tools) {
            if (t.name().equals(name)) {
                try {
                    return t.execute(args);
                } catch (Exception e) {
                    return "工具执行出错: " + e.getMessage();
                }
            }
        }
        return "未找到工具: " + name;
    }

    /**
     * 截断过长的字符串，用于日志打印。
     *
     * @param s      原始字符串
     * @param maxLen 最大长度
     * @return 截断后的字符串，超出部分以 "..." 结尾
     */
    private static String truncate(String s, int maxLen) {
        if (s == null) {
            return "null";
        }
        return s.length() <= maxLen ? s : s.substring(0, maxLen) + "...";
    }
}
