import type { AgentMessage } from '@yagent/runtime-core';
import type { SessionContext } from './SessionContext.js';

export class SessionManager {
  private readonly sessions = new Map<string, SessionContext>();

  getOrCreate(input: {sessionId:string; tenantId:number; userId:number}): SessionContext {
    const existing = this.sessions.get(input.sessionId);
    if (existing) {
      if (existing.tenantId !== input.tenantId || existing.userId !== input.userId) throw new Error('Session tenant/user mismatch');
      return existing;
    }
    const now=Date.now();
    const session: SessionContext={...input,createdAt:now,updatedAt:now,activeCapabilities:[],messages:[]};
    this.sessions.set(input.sessionId,session); return session;
  }
  get(id:string){return this.sessions.get(id);}
  addMessage(id:string,message:AgentMessage){const s=this.sessions.get(id);if(!s)throw new Error('Session not found');s.messages.push(message);s.updatedAt=Date.now();}
  activateCapability(id:string,code:string){const s=this.sessions.get(id);if(!s)throw new Error('Session not found');if(!s.activeCapabilities.includes(code))s.activeCapabilities.push(code);s.updatedAt=Date.now();}
  clear(id:string){this.sessions.delete(id);}
}
