import type { AgentRuntime } from './AgentRuntime.js';
export class AgentService { constructor(private readonly runtime:AgentRuntime){} chat(context:{tenantId:number;userId:number;sessionId:string},message:string){return this.runtime.run(context,message);} }
