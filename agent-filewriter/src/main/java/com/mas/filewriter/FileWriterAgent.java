package com.mas.filewriter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.modelcontext.mcp.server.McpServer;
import com.modelcontext.mcp.tool.Tool;
import com.modelcontext.mcp.tool.ToolRequest;
import com.modelcontext.mcp.tool.ToolResponse;

/**
 * Agent that exposes a saveFile tool using MCP.
 */
public class FileWriterAgent {

    private static final Logger LOGGER = Logger.getLogger(FileWriterAgent.class.getName());

    public static void main(String[] args) throws Exception {
        McpServer server = new McpServer();
        server.registerTool(saveFileTool());
        server.start();
    }

    private static Tool saveFileTool() {
        return Tool.builder()
                .name("saveFile")
                .description("Save content to a file")
                .addParameter("filename", String.class)
                .addParameter("content", String.class)
                .handler(FileWriterAgent::handleSaveFile)
                .build();
    }

    private static ToolResponse handleSaveFile(ToolRequest request) {
        String filename = request.getString("filename");
        String content = request.getString("content");

        String outputDirEnv = System.getenv().getOrDefault("OUTPUT_DIR", "output");
        Path outDir = Paths.get(outputDirEnv);
        Path filePath = outDir.resolve(filename);

        Map<String, Object> result = new HashMap<>();
        try {
            Files.createDirectories(outDir);
            Files.writeString(filePath, content == null ? "" : content, StandardCharsets.UTF_8);
            result.put("status", "OK");
            result.put("path", filePath.toAbsolutePath().toString());
            LOGGER.info("Wrote file: " + filePath.toAbsolutePath());
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Failed to write file: " + filePath.toAbsolutePath(), ex);
            result.put("status", "FAIL");
            result.put("path", filePath.toAbsolutePath().toString());
        }
        return ToolResponse.from(result);
    }
}
