export type AgentRole = 'system' | 'user' | 'assistant' | 'tool';

export interface RuntimeToolDefinition {
  name: string;
  description: string;
  inputSchema: Record<string, unknown>;
}

export interface AgentToolCall {
  id: string;
  name: string;
  arguments: string;
}

export interface AgentMessage {
  role: AgentRole;
  content: string | null;
  toolCalls?: AgentToolCall[];
  toolCallId?: string;
}

export interface AgentStepInput {
  messages: AgentMessage[];
  tools: RuntimeToolDefinition[];
}

export interface AgentStepResult {
  content: string | null;
  toolCalls: AgentToolCall[];
}

export interface AgentRuntimeAdapter {
  runStep(input: AgentStepInput): Promise<AgentStepResult>;
}
