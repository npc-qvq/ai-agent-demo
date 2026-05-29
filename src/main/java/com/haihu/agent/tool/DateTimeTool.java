package com.haihu.agent.tool;

import com.haihu.agent.Tool;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 返回当前本地日期时间的工具，格式为 yyyy-MM-dd HH:mm:ss。
 */
public class DateTimeTool implements Tool {

    /**
     * 返回工具名称 get_datetime。
     *
     * @return 工具名称
     */
    @Override
    public String name() {
        return "get_datetime";
    }

    /**
     * 返回工具功能描述，LLM 据此判断何时调用该工具。
     *
     * @return 工具描述
     */
    @Override
    public String description() {
        return "获取当前日期和时间。";
    }

    /**
     * 返回工具参数的 JSON Schema 定义，该工具无需参数。
     *
     * @return 空属性的 JSON Schema 对象
     */
    @Override
    public Map<String, Object> parameters() {
        return Map.of("type", "object", "properties", Map.of());
    }

    /**
     * 无参数，直接返回当前日期时间的格式化字符串。
     *
     * @param input 未使用（工具无需参数）
     * @return 格式为 yyyy-MM-dd HH:mm:ss 的当前日期时间字符串
     */
    @Override
    public String execute(String input) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
