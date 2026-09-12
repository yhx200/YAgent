import type { RuntimeToolDefinition } from '@yagent/runtime-core';

export interface ToolBinding {
  llmName:string;
  definition:RuntimeToolDefinition;
  capabilityCode:string;
  toolId:string;
  version:string;
  toolName:string;
  entrypoint:string;
  packageDir:string;
  timeoutMs:number;
}

function sanitize(name:string){return name.replace(/[^A-Za-z0-9_-]/g,'_');}

export class ToolRegistry {
  private readonly bySession=new Map<string,Map<string,ToolBinding>>();
  register(sessionId:string,binding:Omit<ToolBinding,'llmName'>):ToolBinding{
    const registry=this.bySession.get(sessionId)??new Map<string,ToolBinding>(); this.bySession.set(sessionId,registry);
    let base=sanitize(binding.toolName), name=base, i=2;
    while(registry.has(name) && registry.get(name)?.toolName!==binding.toolName){name=`${base}_${i++}`;}
    const full={...binding,llmName:name,definition:{...binding.definition,name}};
    registry.set(name,full); return full;
  }
  list(sessionId:string){return [...(this.bySession.get(sessionId)?.values()??[])];}
  get(sessionId:string,llmName:string){return this.bySession.get(sessionId)?.get(llmName);}
  clear(sessionId:string){this.bySession.delete(sessionId);}
}
