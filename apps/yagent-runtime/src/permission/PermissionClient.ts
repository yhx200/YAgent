import type { PlatformClient } from '../platform/PlatformClient.js';
export class PermissionClient {
  constructor(private readonly platform:PlatformClient){}
  async requireAllow(input:{tenantId:number;userId:number;sessionId:string;toolId:string;version:string;toolName:string;stage:'ACTIVATE'|'EXECUTE'}){
    const result=await this.platform.evaluatePermission(input);
    if(result.decision==='ALLOW')return;
    if(result.decision==='REQUIRE_APPROVAL')throw new Error(`Tool requires approval: ${result.reasons.join('; ')}`);
    throw new Error(`Tool denied: ${result.reasons.join('; ')}`);
  }
}
