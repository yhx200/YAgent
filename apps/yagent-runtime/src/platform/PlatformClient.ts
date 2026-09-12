export interface CapabilitySearchItem { capabilityCode:string; name:string; description?:string; category?:string; score:number; providers:Array<{type:string;toolId:string;version:string;toolName:string;installed:boolean}>; }
export interface ResolvedTool { toolId:string; version:string; toolName:string; displayName?:string; description?:string; runtimeType:string; protocolVersion?:string; entrypoint:string; inputSchema:Record<string,unknown>; timeoutMs:number; packageInfo:{objectKey:string;sha256:string;signature?:string}; permissions:Array<{type:string;value:string}>; }
export interface DownloadTicket { url:string; expiresAt:string; sha256:string; signature?:string; objectKey:string; }
export type PermissionDecision='ALLOW'|'DENY'|'REQUIRE_APPROVAL';

export class PlatformClient {
  constructor(private readonly baseUrl:string){}

  private async request<T>(path:string, init?:RequestInit):Promise<T>{
    const response=await fetch(this.baseUrl+path,{...init,headers:{'Content-Type':'application/json',...(init?.headers??{})}});
    if(!response.ok) throw new Error(`Platform ${path} failed: ${response.status} ${await response.text()}`);
    return await response.json() as T;
  }

  searchCapabilities(input:{tenantId:number;query:string;limit:number}){
    return this.request<{items:CapabilitySearchItem[]}>('/inner/v1/capabilities/search',{method:'POST',body:JSON.stringify(input)});
  }
  resolveInstallation(input:{tenantId:number;capabilityCode:string}){
    return this.request<{capabilityCode:string;tools:ResolvedTool[]}>('/inner/v1/installations/resolve',{method:'POST',body:JSON.stringify(input)});
  }
  evaluatePermission(input:{tenantId:number;userId:number;sessionId:string;toolId:string;version:string;toolName:string;stage:'ACTIVATE'|'EXECUTE'}){
    return this.request<{decision:PermissionDecision;reasons:string[]}>('/inner/v1/permissions/evaluate',{method:'POST',body:JSON.stringify(input)});
  }
  downloadTicket(toolId:string,version:string){
    return this.request<DownloadTicket>('/inner/v1/tool-packages/download-ticket',{method:'POST',body:JSON.stringify({toolId,version})});
  }
  reportAudit(input:Record<string,unknown>){
    return this.request<{success:boolean}>('/inner/v1/audit-events',{method:'POST',body:JSON.stringify(input)});
  }
}
