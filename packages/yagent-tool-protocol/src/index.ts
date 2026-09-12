import { z } from 'zod';

export const ToolFunctionSchema = z.object({
  name: z.string().min(1),
  description: z.string().default(''),
  inputSchema: z.record(z.string(), z.unknown()),
  outputSchema: z.record(z.string(), z.unknown()).optional()
});

export const YagentToolManifestSchema = z.object({
  schemaVersion: z.string(),
  id: z.string().min(1),
  version: z.string().min(1),
  name: z.string().min(1),
  displayName: z.string().optional(),
  description: z.string().optional(),
  publisher: z.object({ id: z.string(), name: z.string() }).optional(),
  runtime: z.object({ type: z.enum(['node','python','wasm']), entry: z.string().min(1) }),
  capabilities: z.array(z.object({ code: z.string(), tool: z.string() })),
  tools: z.array(ToolFunctionSchema),
  permissions: z.object({
    network: z.object({ enabled: z.boolean().default(false), allow: z.array(z.string()).default([]) }).optional(),
    filesystem: z.object({ read: z.array(z.string()).default([]), write: z.array(z.string()).default([]) }).optional(),
    shell: z.boolean().default(false)
  }).default({ shell: false }),
  limits: z.object({ timeoutMs: z.number().int().positive().default(10000), memoryMb: z.number().int().positive().default(128) }).optional()
});

export type YagentToolManifest = z.infer<typeof YagentToolManifestSchema>;
export type ToolFunction = z.infer<typeof ToolFunctionSchema>;

export function parseToolManifest(input: unknown): YagentToolManifest {
  return YagentToolManifestSchema.parse(input);
}
