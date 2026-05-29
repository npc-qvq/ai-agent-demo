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

## 内置工具

| 工具 | 名称 | 功能 |
|------|------|------|
| DateTimeTool | `get_datetime` | 获取当前日期时间 |
| FileReadTool | `read_file` | 读取文件内容（≤1MB） |
| FileWriteTool | `write_file` | 写入内容到文件 |
