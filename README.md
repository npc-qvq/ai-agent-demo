# AI Agent Demo

基于 OpenAI 兼容 API 的轻量级 Java Agent 框架，支持工具调用（Tool Calling）。

## 架构

```
用户输入 → LLM 推理 → 工具调用 → 工具结果 → LLM 最终回复
```

核心循环最多迭代 10 轮，LLM 可多次调用工具直到给出文本回复。

## 项目结构

```
src/main/java/com/haihu/agent/
├── Agent.java              # Agent 核心循环
├── AgentApplication.java   # 命令行入口
├── AgentConfig.java        # 默认配置
├── LLMClient.java          # LLM API 客户端（OpenAI 兼容）
├── Message.java            # 消息模型
├── Tool.java               # 工具接口
├── ToolCall.java           # 工具调用模型
├── ToolResult.java         # 工具调用结果
└── tool/
    ├── DateTimeTool.java   # 获取当前时间
    ├── FileReadTool.java   # 读取文件
    ├── FileWriteTool.java  # 写入文件
    └── JsonNodeExtractor.java
```

## 配置

编辑 `AgentConfig.java`：

```java
public static final String API_KEY  = "sk-xxx";
public static final String BASE_URL = "https://api.deepseek.com";
public static final String MODEL    = "deepseek-chat";
```

也支持通过环境变量或 `-D` 参数覆盖：`API_KEY` / `BASE_URL` / `MODEL`。

## 运行

```bash
mvn clean package -q
java -jar target/ai-agent-demo-1.0-SNAPSHOT.jar
```

## 推荐阅读顺序

如果你是 Java 开发者，建议按下面顺序看源码：

```text
AgentApplication.java
  ↓
AgentConfig.java
  ↓
Tool.java
  ↓
DateTimeTool.java / FileReadTool.java / FileWriteTool.java
  ↓
LLMClient.java
  ↓
Agent.java
  ↓
Message.java / ToolCall.java
```

先看 `AgentApplication.java`，理解程序如何启动、如何注册工具。
再看 `Tool.java`，理解 Agent 暴露给 LLM 的工具长什么样。
最后看 `Agent.java`，理解 LLM 返回 `tool_calls` 后，本地如何执行工具并把结果继续发给 LLM。

## 第一次运行可以这样试

启动程序后，可以先输入不涉及工具的问题：

```text
你好，介绍一下你自己
```

再输入一个会触发工具调用的问题：

```text
现在几点？
```

如果模型决定调用工具，控制台会看到类似日志：

```text
[Agent] Iteration 1...
[Agent] LLM requested 1 tool call(s)
[Agent]   Calling tool: get_datetime({})
[Agent]   Tool result: 2026-06-01 20:30:00
```

这就是 Agent 的最小闭环：LLM 不直接回答，而是先请求调用本地工具，本地工具执行后再把结果交回 LLM。

## 内置工具

| 工具 | 名称 | 功能 |
|------|------|------|
| DateTimeTool | `get_datetime` | 获取当前日期时间 |
| FileReadTool | `read_file` | 读取文件内容（≤1MB） |
| FileWriteTool | `write_file` | 写入内容到文件 |

## 如何新增一个工具

新增工具时，只需要实现 `Tool` 接口，然后在 `AgentApplication.java` 的工具列表中注册。

示例：新增一个简单的计算器工具。

```java
package com.haihu.agent.tool;

import com.haihu.agent.Tool;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 计算两个数字相加结果的工具。
 */
public class CalculatorTool implements Tool {

    /**
     * 返回工具名称，LLM 会通过该名称发起工具调用。
     *
     * @return 工具名称
     */
    @Override
    public String name() {
        return "calculator_add";
    }

    /**
     * 返回工具描述，帮助 LLM 判断什么时候使用该工具。
     *
     * @return 工具描述
     */
    @Override
    public String description() {
        return "计算两个数字相加的结果。";
    }

    /**
     * 返回工具参数定义，告诉 LLM 需要传入 a 和 b 两个数字。
     *
     * @return JSON Schema 参数定义
     */
    @Override
    public Map<String, Object> parameters() {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");

        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("a", Map.of("type", "number", "description", "第一个数字"));
        properties.put("b", Map.of("type", "number", "description", "第二个数字"));

        schema.put("properties", properties);
        schema.put("required", java.util.List.of("a", "b"));
        return schema;
    }

    /**
     * 执行工具逻辑。
     *
     * @param input LLM 传入的 JSON 参数
     * @return 计算结果
     */
    @Override
    public String execute(String input) {
        return "这里解析 input 后返回 a + b 的结果";
    }
}
```

然后在 `AgentApplication.java` 中注册：

```java
List<Tool> tools = List.of(
        new FileReadTool(),
        new FileWriteTool(),
        new DateTimeTool(),
        new CalculatorTool()
);
```

新增工具的关键点是：

- `name()` 要稳定，LLM 会用它来指定工具。
- `description()` 要写清楚用途，模型靠它判断是否调用。
- `parameters()` 要描述参数结构，模型靠它生成 JSON 参数。
- `execute()` 只负责本地业务逻辑，入参是模型生成的 JSON 字符串。
