import 'dotenv/config';
import {DeepSeekAgentRuntimeAdapter} from '@yagent/runtime-adapter-deepseek';
import {PlatformClient} from './platform/PlatformClient.js';
import {SessionManager} from './session/SessionManager.js';
import {CapabilityResolver} from './capability/CapabilityResolver.js';
import {PermissionClient} from './permission/PermissionClient.js';
import {ToolMaterializer} from './tool/ToolMaterializer.js';
import {ToolRegistry} from './tool/ToolRegistry.js';
import {ToolManager} from './tool/ToolManager.js';
import {ToolRuntimeManager} from './sandbox/ToolRuntimeManager.js';
import {AuditClient} from './audit/AuditClient.js';
import {ToolInvoker} from './tool/ToolInvoker.js';
import {MockAgentRuntimeAdapter} from './adapter/MockAgentRuntimeAdapter.js';
import {AgentRuntime} from './agent/AgentRuntime.js';
import {AgentService} from './agent/AgentService.js';
import {RuntimeApplication} from './application/RuntimeApplication.js';

const platform = new PlatformClient(process.env.YAGENT_PLATFORM_BASE_URL ?? 'http://localhost:8080');
const sessions = new SessionManager();
const resolver = new CapabilityResolver(platform);
const permissions = new PermissionClient(platform);
const materializer = new ToolMaterializer(platform, process.env.YAGENT_TOOL_CACHE_ROOT ?? './runtime-data/tool-cache');
const registry = new ToolRegistry();
const toolManager = new ToolManager(resolver, sessions, permissions, materializer, registry);
const runner = new ToolRuntimeManager(process.env.YAGENT_RUNNER_BASE_URL ?? 'http://localhost:8090');
const audit = new AuditClient(platform);
const invoker = new ToolInvoker(registry, permissions, runner, audit);

const llmMode = (process.env.YAGENT_LLM_MODE ?? 'mock').toLowerCase();
const adapter = llmMode === 'deepseek'
    ? new DeepSeekAgentRuntimeAdapter({
        apiKey: process.env.DEEPSEEK_API_KEY ?? '',
        baseURL: process.env.DEEPSEEK_BASE_URL,
        model: process.env.DEEPSEEK_MODEL
    })
    : new MockAgentRuntimeAdapter();
if (llmMode === 'deepseek' && !process.env.DEEPSEEK_API_KEY) throw new Error('YAGENT_LLM_MODE=deepseek requires DEEPSEEK_API_KEY');

const runtime = new AgentRuntime(adapter, sessions, toolManager, invoker);
const service = new AgentService(runtime);
new RuntimeApplication(service, sessions).start(Number(process.env.PORT ?? '3000'));
