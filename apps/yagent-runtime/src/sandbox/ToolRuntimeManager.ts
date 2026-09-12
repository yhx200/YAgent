import { randomUUID } from 'node:crypto';
export class ToolRuntimeManager {
  constructor(private readonly runnerBaseUrl:string){}
  async invoke(input:{packageDir:string;entrypoint:string;toolName:string;args:Record<string,unknown>;timeoutMs:number}){
    const response=await fetch(`${this.runnerBaseUrl}/invoke`,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({requestId:randomUUID(),packageDir:input.packageDir,entrypoint:input.entrypoint,toolName:input.toolName,arguments:input.args,timeoutMs:input.timeoutMs})});
    const body=await response.json() as any;
    if(!response.ok||!body.success)throw new Error(body.error??`Runner failed ${response.status}`);
    return body.result;
  }
}
