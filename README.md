# Micro-Agent Services MVP (MAS-MVP)

MAS-MVP demonstrates the **Micro-Agent Services (MAS)** architecture using the Model Context Protocol (MCP). Each agent exposes tools over MCP and can be started independently as a microservice. An orchestrator coordinates the agents to turn a requirement into runnable code.

```
+----------------------+      MCP Client
|   MAS Orchestrator   |
+----------------------+-----+---+------
          |                 |   |
          v                 v   v
+---------+  +-------------+  +-----------+
| Analyst |  | Developer   |  |  Tester   |
| Agent   |  | Agent       |  |  Agent    |
+---------+  +-------------+  +-----------+
(MCP srv)    (MCP srv)       (MCP srv)
```

## Architecture

MAS defines a set of lightweight agents communicating via MCP. Each agent exposes one or more tools and runs as its own process. The orchestrator invokes these tools in sequence to deliver a workflow.

### Agents

- **Analyst Agent** (`agent-analyst`)
  - Exposes `analyzeRequirement` to convert a plain requirement into user stories.
  - Listens on port `8080` by default.

- **Developer Agent** (`agent-developer`)
  - Exposes `generateCode` that returns a Java method stub from a user story.
  - Listens on port `8081` by default.

- **Tester Agent** (`agent-tester`)
  - Exposes `runTest` to run a simple check on provided code.
  - Listens on port `8082` by default.

### Orchestrator

The `orchestrator` module connects to each agent over MCP. It requests a user requirement, asks the Analyst for stories, requests code from the Developer and finally runs the Tester. The orchestrator prints the generated code and test results.

## Building

Each module is a standalone Maven project. Compile them individually:

```bash
cd agent-analyst && mvn package
cd ../agent-developer && mvn package
cd ../agent-tester && mvn package
cd ../orchestrator && mvn package
```

The build produces `*-jar-with-dependencies.jar` under each module's `target` directory.

## Running the Agents

Start the agents in separate terminals:

```bash
# Terminal 1
java -jar agent-analyst/target/agent-analyst-1.0-SNAPSHOT-jar-with-dependencies.jar

# Terminal 2
java -jar agent-developer/target/agent-developer-1.0-SNAPSHOT-jar-with-dependencies.jar

# Terminal 3
java -jar agent-tester/target/agent-tester-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Running the Orchestrator

With the agents running, execute:

```bash
java -jar orchestrator/target/orchestrator-1.0-SNAPSHOT-jar-with-dependencies.jar
```

The orchestrator prompts for a requirement and then prints a user story, the generated code and the test result.

### Example Workflow

1. Enter a requirement when prompted, e.g. `calculate the sum of two numbers`.
2. The Analyst Agent returns a user story.
3. The Developer Agent generates a Java method stub implementing the story.
4. The Tester Agent validates the code and reports success or failure.

This workflow illustrates how specialized agents collaborate via MCP to automate a simple software task.
