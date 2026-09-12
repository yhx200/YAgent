export interface ToolDefinition {

    name: string;

    description: string;

    inputSchema: Record<
        string,
        unknown
    >;
}


export interface RuntimeTool {

    definition: ToolDefinition;


    execute(
        args: Record<
            string,
            unknown
        >
    ): Promise<unknown>;
}
