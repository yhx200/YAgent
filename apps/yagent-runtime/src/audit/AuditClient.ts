import type { PlatformClient } from '../platform/PlatformClient.js';
export class AuditClient { constructor(private readonly platform:PlatformClient){} report(event:Record<string,unknown>){return this.platform.reportAudit(event).catch(e=>console.error('[audit] report failed',e));} }
