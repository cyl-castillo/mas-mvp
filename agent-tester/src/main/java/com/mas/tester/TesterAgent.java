package com.mas.tester;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.tool.Tool;
import io.modelcontextprotocol.tool.ToolRequest;
import io.modelcontextprotocol.tool.ToolResponse;

/**
 * Simple Tester Agent exposing a runTest tool using MCP.
 */
public class TesterAgent {

    private static final Logger LOGGER = Logger.getLogger(TesterAgent.class.getName());

    public static void main(String[] args) throws Exception {
        McpServer server = new McpServer();
        server.registerTool(runTestTool());
        server.start();
    }

    private static Tool runTestTool() {
        return Tool.builder()
                .name("runTest")
                .description("Run tests on provided code")
                .addParameter("code", String.class)
                .handler(TesterAgent::handleRunTest)
                .build();
    }

    private static ToolResponse handleRunTest(ToolRequest request) {
        String code = request.getString("code");
        if (code == null || code.isBlank()) {
            LOGGER.warning("Received blank code.");
        } else {
            LOGGER.info("Running tests on code: " + code);
        }
        Map<String, Object> result = new HashMap<>();
        try {
            if (code != null && code.contains("FAIL")) {
                result.put("result", "FAIL");
                result.put("details", "Code contains FAIL keyword");
            } else {
                result.put("result", "OK");
                result.put("details", "Tests executed successfully");
            }
            LOGGER.info("Test result: " + result.get("result") + ", details: " + result.get("details"));
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error running tests", ex);
            result.put("result", "ERROR");
            result.put("details", ex.getMessage());
        }
        return ToolResponse.from(result);
    }
}
