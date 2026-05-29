package com.haihu.agent;

import com.haihu.agent.tool.DateTimeTool;
import com.haihu.agent.tool.FileReadTool;
import com.haihu.agent.tool.FileWriteTool;

import java.util.List;
import java.util.Scanner;

/**
 * Agent 演示程序的命令行入口。
 *
 * 默认配置定义在 {@link AgentConfig} 中，
 * 可通过环境变量或 JVM 系统属性覆盖 API_KEY / BASE_URL / MODEL。
 * 启动后以交互式命令行方式与 Agent 对话，输入 quit 退出。
 */
public class AgentApplication {

    /**
     * 启动交互式 Agent 命令行。
     *
     * @param args 未使用
     */
    public static void main(String[] args) {
        String apiKey = env("API_KEY", AgentConfig.API_KEY);
        String baseUrl = env("BASE_URL", AgentConfig.BASE_URL);
        String model = env("MODEL", AgentConfig.MODEL);

        if (apiKey == null || baseUrl == null || model == null) {
            System.out.println("请在 AgentConfig 中配置以下信息：");
            System.out.println("  API_KEY  - LLM API key");
            System.out.println("  BASE_URL - API base URL, for example https://api.deepseek.com");
            System.out.println("  MODEL    - Model name, for example deepseek-chat");
            return;
        }

        LLMClient llmClient = new LLMClient(apiKey, baseUrl, model);

        List<Tool> tools = List.of(
                new FileReadTool(),
                new FileWriteTool(),
                new DateTimeTool()
        );

        Agent agent = new Agent(llmClient, tools, AgentConfig.SYSTEM_PROMPT, AgentConfig.MAX_ITERATIONS);

        System.out.println("===== AI Agent Demo =====");
        System.out.println("Model: " + model);
        System.out.println("Tools: " + tools.stream().map(Tool::name).reduce((a, b) -> a + ", " + b).orElse("none"));
        System.out.println("Type 'quit' to exit\n");

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("You> ");
                String input = scanner.nextLine().trim();
                if (input.isBlank()) {
                    continue;
                }
                if ("quit".equalsIgnoreCase(input)) {
                    break;
                }

                String result = agent.run(input);
                System.out.println("Agent> " + result);
                System.out.println();
            }
        }
        System.out.println("Bye!");
    }

    /**
     * 按优先级获取配置值：环境变量 > JVM 系统属性 > 默认值。
     *
     * @param name         配置项名称
     * @param defaultValue AgentConfig 中定义的默认值
     * @return 配置值，未配置时返回默认值
     */
    private static String env(String name, String defaultValue) {
        String val = System.getenv(name);
        if (val != null && !val.isBlank()) {
            return val;
        }
        val = System.getProperty(name);
        if (val != null && !val.isBlank()) {
            return val;
        }
        return defaultValue;
    }
}
