import OpenAI from 'openai';
import type { AgentMessage, AgentRuntimeAdapter, AgentStepInput, AgentStepResult } from '@yagent/runtime-core';

export interface DeepSeekAdapterOptions {
  apiKey: string;
  baseURL?: string;
  model?: string;
}

function toOpenAiMessage(message: AgentMessage): any {
  if (message.role === 'assistant' && message.toolCalls?.length) {
    return {
      role: 'assistant',
      content: message.content,
      tool_calls: message.toolCalls.map(c => ({
        id: c.id,
        type: 'function',
        function: { name: c.name, arguments: c.arguments }
      }))
    };
  }
  if (message.role === 'tool') {
    return { role: 'tool', content: message.content ?? '', tool_call_id: message.toolCallId };
  }
  return { role: message.role, content: message.content ?? '' };
}

export class DeepSeekAgentRuntimeAdapter implements AgentRuntimeAdapter {
  private readonly client: OpenAI;
  private readonly model: string;

  constructor(options: DeepSeekAdapterOptions) {
    this.client = new OpenAI({ apiKey: options.apiKey, baseURL: options.baseURL ?? 'https://api.deepseek.com' });
    this.model = options.model ?? 'deepseek-chat';
  }

  async runStep(input: AgentStepInput): Promise<AgentStepResult> {
    const response = await this.client.chat.completions.create({
      model: this.model,
      messages: input.messages.map(toOpenAiMessage),
      tools: input.tools.length ? input.tools.map(tool => ({
        type: 'function' as const,
        function: { name: tool.name, description: tool.description, parameters: tool.inputSchema }
      })) : undefined,
      tool_choice: input.tools.length ? 'auto' : undefined
    });

    const message = response.choices[0]?.message;
    if (!message) throw new Error('DeepSeek did not return a message');

    return {
      content: message.content ?? null,
      toolCalls: message.tool_calls?.map(call => ({
        id: call.id,
        name: call.function.name,
        arguments: call.function.arguments
      })) ?? []
    };
  }
}
