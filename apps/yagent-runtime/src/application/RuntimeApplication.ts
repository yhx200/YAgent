import express from 'express';
import type { AgentService } from '../agent/AgentService.js';
import type { SessionManager } from '../session/SessionManager.js';
import { createRuntimeRouter } from '../http/RuntimeController.js';
export class RuntimeApplication {
  private readonly app=express();
  constructor(agent:AgentService,sessions:SessionManager){this.app.use(express.json({limit:'2mb'}));this.app.use('/runtime',createRuntimeRouter(agent,sessions));}
  start(port:number){this.app.listen(port,()=>console.log(`[runtime] http://localhost:${port}`));}
}
