package com.haihu.agent;

import java.util.Map;

/**
 * Agent 可向 LLM 暴露的工具接口。
 * <p>
 * 每个工具需要提供名称、描述、参数定义和具体执行逻辑。
 */
public interface Tool {

    /**
     * 工具名称，LLM 通过该名称选择调用哪个工具。
     *
     * @return 工具名称
     */
    String name();

    /**
     * 工具功能描述，LLM 据此判断是否需要调用该工具。
     *
     * @return 工具描述
     */
    String description();

    /**
     * 工具的参数 JSON Schema 定义，用于 LLM 生成正确的调用参数。
     *
     * @return 参数 JSON Schema 对象
     */
    Map<String, Object> parameters();

    /**
     * 执行工具逻辑，入参为 LLM 生成的参数 JSON 字符串。
     *
     * @param input JSON 格式的工具调用参数
     * @return 工具执行结果字符串
     */
    String execute(String input);
}
