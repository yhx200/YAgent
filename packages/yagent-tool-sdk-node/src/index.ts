export type ToolExecutor = (toolName: string, args: Record<string, unknown>) => Promise<unknown>;
export function defineYagentToolExecutor(executor: ToolExecutor): ToolExecutor { return executor; }
