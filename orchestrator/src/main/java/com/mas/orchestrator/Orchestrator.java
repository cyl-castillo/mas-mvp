package com.mas.orchestrator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import com.modelcontext.mcp.client.McpClient;
import com.modelcontext.mcp.tool.ToolRequest;
import com.modelcontext.mcp.tool.ToolResponse;

/**
 * Orchestrator that coordinates Analyst, Developer and Tester agents using MCP.
 */
public class Orchestrator {

    public static void main(String[] args) throws Exception {
        try (McpClient analyst = new McpClient("localhost", 8080);
             McpClient developer = new McpClient("localhost", 8081);
             McpClient tester = new McpClient("localhost", 8082);
             BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.print("Enter a requirement: ");
            String requirement = reader.readLine();

            ToolRequest analyzeReq = ToolRequest.builder()
                    .toolName("analyzeRequirement")
                    .addParam("requirement", requirement)
                    .build();
            ToolResponse analyzeResp = analyst.invoke(analyzeReq);
            List<String> stories = analyzeResp.getList("user_stories", String.class);
            if (stories.isEmpty()) {
                System.out.println("No user stories produced");
                return;
            }
            String story = stories.getFirst();
            System.out.println("User story: " + story);

            ToolRequest devReq = ToolRequest.builder()
                    .toolName("generateCode")
                    .addParam("user_story", story)
                    .build();
            ToolResponse devResp = developer.invoke(devReq);
            String code = devResp.getString("code");
            System.out.println("Generated code:\n" + code);

            ToolRequest testReq = ToolRequest.builder()
                    .toolName("runTest")
                    .addParam("code", code)
                    .build();
            ToolResponse testResp = tester.invoke(testReq);
            String result = testResp.getString("result");
            String details = testResp.getString("details");
            System.out.println("Test result: " + result);
            System.out.println("Details: " + details);
        }
    }
}
