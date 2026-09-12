import { newTraceId, errorMessage } from '@yagent/common';
import type { ToolRegistry } from './ToolRegistry.js';
import type { PermissionClient } from '../permission/PermissionClient.js';
import type { ToolRuntimeManager } from '../sandbox/ToolRuntimeManager.js';
import type { AuditClient } from '../audit/AuditClient.js';

export class ToolInvoker {
  constructor(private readonly registry:ToolRegistry,private readonly permissions:PermissionClient,private readonly runtime:ToolRuntimeManager,private readonly audit:AuditClient){}
  async invoke(input:{tenantId:number;userId:number;sessionId:string;llmName:string;argumentText:string}){
    const binding=this.registry.get(input.sessionId,input.llmName); if(!binding)throw new Error(`Unknown active tool: ${input.llmName}`);
    const args=input.argumentText?JSON.parse(input.argumentText):{};
    await this.permissions.requireAllow({...input,toolId:binding.toolId,version:binding.version,toolName:binding.toolName,stage:'EXECUTE'});
    const traceId=newTraceId(), start=Date.now();
    try{
      const result=await this.runtime.invoke({packageDir:binding.packageDir,entrypoint:binding.entrypoint,toolName:binding.toolName,args,timeoutMs:binding.timeoutMs});
      void this.audit.report({traceId,tenantId:input.tenantId,userId:input.userId,sessionId:input.sessionId,eventType:'TOOL_INVOKE',capabilityCode:binding.capabilityCode,toolId:binding.toolId,toolVersion:binding.version,toolName:binding.toolName,resultStatus:'SUCCESS',durationMs:Date.now()-start,detailJson:JSON.stringify({argKeys:Object.keys(args)})});
      return result;
    }catch(error){
      void this.audit.report({traceId,tenantId:input.tenantId,userId:input.userId,sessionId:input.sessionId,eventType:'TOOL_INVOKE',capabilityCode:binding.capabilityCode,toolId:binding.toolId,toolVersion:binding.version,toolName:binding.toolName,resultStatus:'FAILED',durationMs:Date.now()-start,detailJson:JSON.stringify({error:errorMessage(error)})});
      throw error;
    }
  }
}
