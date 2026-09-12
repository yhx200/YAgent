import type { PlatformClient } from '../platform/PlatformClient.js';
export class CapabilityResolver {
  constructor(private readonly platform:PlatformClient){}
  search(tenantId:number,query:string,limit=5){return this.platform.searchCapabilities({tenantId,query,limit});}
  acquire(tenantId:number,capabilityCode:string){return this.platform.resolveInstallation({tenantId,capabilityCode});}
}
