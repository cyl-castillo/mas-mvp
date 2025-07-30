package com.mas.developer;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.tool.Tool;
import io.modelcontextprotocol.tool.ToolRequest;
import io.modelcontextprotocol.tool.ToolResponse;

/**
 * Simple Developer Agent exposing a generateCode tool using MCP.
 */
public class DeveloperAgent {

    private static final Logger LOGGER = Logger.getLogger(DeveloperAgent.class.getName());

    public static void main(String[] args) throws Exception {
        McpServer server = new McpServer();
        server.registerTool(generateCodeTool());
        server.start();
    }

    private static Tool generateCodeTool() {
        return Tool.builder()
                .name("generateCode")
                .description("Generate a Java method stub from a user story")
                .addParameter("user_story", String.class)
                .handler(DeveloperAgent::handleGenerateCode)
                .build();
    }

    private static ToolResponse handleGenerateCode(ToolRequest request) {
        String story = request.getString("user_story");
        if (story == null || story.isBlank()) {
            LOGGER.warning("Received blank user story.");
        } else {
            LOGGER.info("Generating code for story: " + story);
        }
        try {
            String methodName = toMethodName(story);
            String code = "public void " + methodName + "() {\n    // TODO: implement\n}";

            Map<String, Object> result = new HashMap<>();
            result.put("code", code);
            result.put("language", "Java");
            LOGGER.info("Generated code:\n" + code);
            return ToolResponse.from(result);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error generating code", ex);
            Map<String, Object> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ToolResponse.from(error);
        }
    }

    private static String toMethodName(String story) {
        if (story == null || story.isBlank()) {
            return "method";
        }
        String cleaned = story.replaceAll("[^a-zA-Z0-9 ]", "").trim();
        String[] parts = cleaned.split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String p = parts[i];
            if (i == 0) {
                builder.append(p.toLowerCase());
            } else {
                builder.append(Character.toUpperCase(p.charAt(0)))
                       .append(p.substring(1).toLowerCase());
            }
        }
        return builder.toString();
    }
}
