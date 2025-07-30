package com.mas.analyst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.modelcontext.mcp.server.McpServer;
import com.modelcontext.mcp.tool.Tool;
import com.modelcontext.mcp.tool.ToolRequest;
import com.modelcontext.mcp.tool.ToolResponse;

/**
 * Simple Analyst Agent exposing an analyzeRequirement tool using MCP.
 */
public class AnalystAgent {

    private static final Logger LOGGER = Logger.getLogger(AnalystAgent.class.getName());

    public static void main(String[] args) throws Exception {
        McpServer server = new McpServer();
        server.registerTool(analyzeRequirementTool());
        server.start();
    }

    private static Tool analyzeRequirementTool() {
        return Tool.builder()
                .name("analyzeRequirement")
                .description("Analyze a requirement and return user stories")
                .addParameter("requirement", String.class)
                .handler(AnalystAgent::handleAnalyzeRequirement)
                .build();
    }

    private static ToolResponse handleAnalyzeRequirement(ToolRequest request) {
        String requirement = request.getString("requirement");
        if (requirement == null || requirement.isBlank()) {
            LOGGER.warning("Received blank requirement.");
        } else {
            LOGGER.info("Analyzing requirement: " + requirement);
        }
        try {
            List<String> stories = new ArrayList<>();
            if (requirement != null && !requirement.isBlank()) {
                stories.add("As a user, I want " + requirement);
            }
            Map<String, Object> result = new HashMap<>();
            result.put("user_stories", stories);
            LOGGER.info("Produced stories: " + stories);
            return ToolResponse.from(result);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error analyzing requirement", ex);
            Map<String, Object> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ToolResponse.from(error);
        }
    }
}
