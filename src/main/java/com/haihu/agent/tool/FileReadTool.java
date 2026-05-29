package com.haihu.agent.tool;

import com.haihu.agent.Tool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 读取小文本文件内容的工具，文件大小限制为 1MB。
 */
public class FileReadTool implements Tool {

    /**
     * 返回工具名称 read_file。
     *
     * @return 工具名称
     */
    @Override
    public String name() {
        return "read_file";
    }

    /**
     * 返回工具功能描述，LLM 据此判断何时调用该工具。
     *
     * @return 工具描述
     */
    @Override
    public String description() {
        return "读取指定文件的内容。参数 filePath 是文件的绝对路径。";
    }

    /**
     * 返回工具参数 JSON Schema，包含必填的 filePath 参数。
     *
     * @return 参数 JSON Schema 对象
     */
    @Override
    public Map<String, Object> parameters() {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");

        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("filePath", Map.of("type", "string", "description", "文件的绝对路径"));

        schema.put("properties", properties);
        schema.put("required", java.util.List.of("filePath"));
        return schema;
    }

    /**
     * 读取指定路径的文本文件，文件须存在且不超过 1MB。
     *
     * @param input JSON 格式参数，需包含 filePath 字段
     * @return 文件内容，或错误描述（文件不存在 / 路径非法 / 文件过大 / 读取 IO 异常）
     */
    @Override
    public String execute(String input) {
        try {
            JsonNodeExtractor extractor = new JsonNodeExtractor(input);
            String filePath = extractor.getString("filePath");
            if (filePath == null || filePath.isBlank()) {
                return "错误: 缺少 filePath 参数";
            }

            Path path = Path.of(filePath);
            if (!Files.exists(path)) {
                return "文件不存在: " + filePath;
            }
            if (!Files.isRegularFile(path)) {
                return "路径不是文件: " + filePath;
            }

            long size = Files.size(path);
            if (size > 1024 * 1024) {
                return "文件过大 (" + (size / 1024) + " KB)，请换一个小文件读取";
            }

            return Files.readString(path);
        } catch (IOException e) {
            return "读取文件失败: " + e.getMessage();
        }
    }
}
