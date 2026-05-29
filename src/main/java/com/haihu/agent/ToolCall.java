package com.haihu.agent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * LLM 响应中请求的工具调用，包含唯一标识、类型和函数信息。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ToolCall {

    /**
     * 工具调用的唯一标识 ID。
     */
    private String id;

    /**
     * 调用类型，通常为 "function"。
     */
    private String type;

    /**
     * 被调用的函数名称及参数。
     */
    private Function function;

    /**
     * 获取工具调用 ID。
     *
     * @return 工具调用 ID
     */
    public String id() {
        return id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取工具调用的类型。
     *
     * @return 调用类型
     */
    public String type() {
        return type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取被调用的函数信息。
     *
     * @return 函数名称和参数
     */
    public Function function() {
        return function;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    /**
     * 工具调用中的函数详情，包含函数名和 JSON 格式的调用参数。
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Function {

        /**
         * 被调用函数的名称。
         */
        private String name;

        /**
         * JSON 格式的函数调用参数。
         */
        private String arguments;

        /**
         * 获取函数名称。
         *
         * @return 函数名
         */
        public String name() {
            return name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        /**
         * 获取函数调用的参数 JSON 字符串。
         *
         * @return 参数 JSON
         */
        public String arguments() {
            return arguments;
        }

        public String getArguments() {
            return arguments;
        }

        public void setArguments(String arguments) {
            this.arguments = arguments;
        }
    }
}
