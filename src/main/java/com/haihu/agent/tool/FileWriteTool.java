package com.haihu.agent.tool;

import com.haihu.agent.Tool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 将文本内容写入指定文件的工具，目录不存在时自动创建父目录。
 */
public class FileWriteTool implements Tool {

    /**
     * 返回工具名称 write_file。
     *
     * @return 工具名称
     */
    @Override
    public String name() {
        return "write_file";
    }

    /**
     * 返回工具功能描述，LLM 据此判断何时调用该工具。
     *
     * @return 工具描述
     */
    @Override
    public String description() {
        return "将内容写入指定文件。参数 filePath 是文件的绝对路径，content 是要写入的内容。";
    }

    /**
     * 返回工具参数 JSON Schema，包含必填的 filePath 和 content 参数。
     *
     * @return 参数 JSON Schema 对象
     */
    @Override
    public Map<String, Object> parameters() {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");

        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("filePath", Map.of("type", "string", "description", "文件的绝对路径"));
        properties.put("content", Map.of("type", "string", "description", "要写入的内容"));

        schema.put("properties", properties);
        schema.put("required", java.util.List.of("filePath", "content"));
        return schema;
    }

    /**
     * 将内容写入指定文件，父目录不存在时自动创建。
     *
     * @param input JSON 格式参数，需包含 filePath 和 content 字段
     * @return 成功提示或错误描述
     */
    @Override
    public String execute(String input) {
        try {
            JsonNodeExtractor extractor = new JsonNodeExtractor(input);
            String filePath = extractor.getString("filePath");
            String content = extractor.getString("content");

            if (filePath == null || filePath.isBlank()) {
                return "错误: 缺少 filePath 参数";
            }
            if (content == null) {
                return "错误: 缺少 content 参数";
            }

            Path path = Path.of(filePath);
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(path, content);
            return "文件写入成功: " + filePath;
        } catch (IOException e) {
            return "写入文件失败: " + e.getMessage();
        }
    }
}
