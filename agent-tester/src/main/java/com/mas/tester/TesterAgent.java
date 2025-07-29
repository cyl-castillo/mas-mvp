package com.mas.tester;

import java.util.HashMap;
import java.util.Map;

import com.modelcontext.mcp.server.McpServer;
import com.modelcontext.mcp.tool.Tool;
import com.modelcontext.mcp.tool.ToolRequest;
import com.modelcontext.mcp.tool.ToolResponse;

/**
 * Simple Tester Agent exposing a runTest tool using MCP.
 */
public class TesterAgent {

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
        Map<String, Object> result = new HashMap<>();
        if (code != null && code.contains("FAIL")) {
            result.put("result", "FAIL");
            result.put("details", "Code contains FAIL keyword");
        } else {
            result.put("result", "OK");
            result.put("details", "Tests executed successfully");
        }
        return ToolResponse.from(result);
    }
}
