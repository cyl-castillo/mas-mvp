package com.mas.orchestrator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.capabilities.ClientCapabilities;
import io.modelcontextprotocol.schema.McpSchema.CallToolRequest;
import io.modelcontextprotocol.schema.McpSchema.CallToolResult;
import io.modelcontextprotocol.transport.McpTransport;
import io.modelcontextprotocol.transport.SseClientTransport;

/**
 * Orchestrator that coordinates Analyst, Developer and Tester agents using MCP.
 */
public class Orchestrator {

    public static void main(String[] args) throws Exception {
        try (McpSyncClient analyst = createClient("localhost", 8080);
             McpSyncClient developer = createClient("localhost", 8081);
             McpSyncClient tester = createClient("localhost", 8082);
             McpSyncClient fileWriter = createClient("localhost", 8083);
             BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

            analyst.initialize();
            developer.initialize();
            tester.initialize();
            fileWriter.initialize();

            System.out.print("Enter a requirement: ");
            String requirement = reader.readLine();

            CallToolResult analyzeResp = analyst.callTool(
                    new CallToolRequest("analyzeRequirement",
                            Map.of("requirement", requirement)));
            List<String> stories = (List<String>) analyzeResp.result()
                    .get("user_stories");
            if (stories == null || stories.isEmpty()) {
                System.out.println("No user stories produced");
                return;
            }
            String story = stories.get(0);
            System.out.println("User story: " + story);

            CallToolResult devResp = developer.callTool(
                    new CallToolRequest("generateCode",
                            Map.of("user_story", story)));
            String code = (String) devResp.result().get("code");
            System.out.println("Generated code:\n" + code);

            CallToolResult saveResp = fileWriter.callTool(
                    new CallToolRequest("saveFile",
                            Map.of("filename", "ProductFilter.java",
                                   "content", code)));
            String saveStatus = (String) saveResp.result().get("status");
            String savePath = (String) saveResp.result().get("path");
            System.out.println("FileWriter status: " + saveStatus);
            System.out.println("File path: " + savePath);

            CallToolResult testResp = tester.callTool(
                    new CallToolRequest("runTest",
                            Map.of("code", code)));
            String result = (String) testResp.result().get("result");
            String details = (String) testResp.result().get("details");
            System.out.println("Test result: " + result);
            System.out.println("Details: " + details);
        }
    }

    private static McpSyncClient createClient(String host, int port) {
        McpTransport transport = new SseClientTransport(URI.create("http://" + host + ":" + port));
        return McpClient.sync(transport)
                .requestTimeout(Duration.ofSeconds(10))
                .capabilities(ClientCapabilities.builder().build())
                .build();
    }
}
