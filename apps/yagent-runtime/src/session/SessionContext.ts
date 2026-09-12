import type { AgentMessage } from '@yagent/runtime-core';

export interface SessionContext {
  sessionId: string;
  tenantId: number;
  userId: number;
  createdAt: number;
  updatedAt: number;
  activeCapabilities: string[];
  messages: AgentMessage[];
}
