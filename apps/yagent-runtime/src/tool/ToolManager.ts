import type {RuntimeToolDefinition} from '@yagent/runtime-core';
import type {CapabilityResolver} from '../capability/CapabilityResolver.js';
import type {SessionManager} from '../session/SessionManager.js';
import type {PermissionClient} from '../permission/PermissionClient.js';
import type {ToolMaterializer} from './ToolMaterializer.js';
import type {ToolRegistry} from './ToolRegistry.js';

export const BOOTSTRAP_TOOLS: RuntimeToolDefinition[] = [
    {
        name: 'capability_search',
        description: '搜索完成用户任务所需的动态能力。缺少能力时先调用。',
        inputSchema: {
            type: 'object',
            properties: {query: {type: 'string'}},
            required: ['query'],
            additionalProperties: false
        }
    },
    {
        name: 'capability_acquire',
        description: '获取并激活一个 capability。参数必须使用 capability_search 返回的 capabilityCode。',
        inputSchema: {
            type: 'object',
            properties: {capabilityCode: {type: 'string'}},
            required: ['capabilityCode'],
            additionalProperties: false
        }
    },
    {
        name: 'capability_list',
        description: '列出当前 Session 已激活能力。',
        inputSchema: {type: 'object', properties: {}, additionalProperties: false}
    }
];

export class ToolManager {
    constructor(private readonly resolver: CapabilityResolver, private readonly sessions: SessionManager, private readonly permissions: PermissionClient, private readonly materializer: ToolMaterializer, private readonly registry: ToolRegistry) {
    }

    schemas(sessionId: string): RuntimeToolDefinition[] {
        return [...BOOTSTRAP_TOOLS, ...this.registry.list(sessionId).map(x => x.definition)];
    }

    async invokeBootstrap(input: {
        tenantId: number;
        userId: number;
        sessionId: string;
        name: string;
        argumentText: string
    }) {
        const args = input.argumentText ? JSON.parse(input.argumentText) : {};
        if (input.name === 'capability_search') return await this.resolver.search(input.tenantId, String(args.query ?? ''), 5);
        if (input.name === 'capability_list') return {items: this.sessions.get(input.sessionId)?.activeCapabilities ?? []};
        if (input.name !== 'capability_acquire') throw new Error(`Unknown bootstrap tool ${input.name}`);

        const capabilityCode = String(args.capabilityCode ?? '');
        if (!capabilityCode) throw new Error('capabilityCode is required');
        const resolved = await this.resolver.acquire(input.tenantId, capabilityCode);
        if (!resolved.tools.length) throw new Error(`No installed provider for capability ${capabilityCode}`);

        const activated = [];
        for (const tool of resolved.tools) {
            await this.permissions.requireAllow({
                tenantId: input.tenantId,
                userId: input.userId,
                sessionId: input.sessionId,
                toolId: tool.toolId,
                version: tool.version,
                toolName: tool.toolName,
                stage: 'ACTIVATE'
            });
            const materialized = await this.materializer.materialize(tool);
            const binding = this.registry.register(input.sessionId, {
                definition: {
                    name: tool.toolName,
                    description: tool.description ?? tool.displayName ?? tool.toolName,
                    inputSchema: tool.inputSchema
                },
                capabilityCode,
                toolId: tool.toolId,
                version: tool.version,
                toolName: tool.toolName,
                entrypoint: materialized.entrypoint,
                packageDir: materialized.packageDir,
                timeoutMs: materialized.timeoutMs
            });
            activated.push({
                llmName: binding.llmName,
                toolId: tool.toolId,
                version: tool.version,
                toolName: tool.toolName
            });
        }
        this.sessions.activateCapability(input.sessionId, capabilityCode);
        return {capabilityCode, activated};
    }
}
