package com.haihu.agent;

/**
 * Agent 默认配置。
 *
 * 可通过环境变量或 JVM 系统属性覆盖：
 *   API_KEY / BASE_URL / MODEL
 */
public final class AgentConfig {

    private AgentConfig() {
    }

    /**
     * LLM API 密钥。
     */
    public static final String API_KEY = "sk-xxx";

    /**
     * LLM API 基础地址。
     */
    public static final String BASE_URL = "https://api.deepseek.com";

    /**
     * 模型名称。
     */
    public static final String MODEL = "deepseek-v4-flash";

    /**
     * 系统提示词。
     */
    public static final String SYSTEM_PROMPT = String.join("\n",
            "你是一个有用的 AI 助手。",
            "你可以使用工具来读取文件、写入文件、获取当前时间。",
            "当用户要求执行操作时，请使用合适的工具。",
            "回答时使用中文。");

    /**
     * Agent 最大迭代次数。
     */
    public static final int MAX_ITERATIONS = 10;
}
