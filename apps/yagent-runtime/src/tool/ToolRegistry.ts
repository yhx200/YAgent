import {
    RuntimeTool
} from './Tool.js';


export class ToolRegistry {

    private readonly tools =
        new Map<string, RuntimeTool>();


    register(
        tool: RuntimeTool
    ): void {

        const name =
            tool.definition.name;


        if (this.tools.has(name)) {

            throw new Error(
                `工具已存在: ${name}`
            );
        }


        this.tools.set(
            name,
            tool
        );
    }


    get(
        name: string
    ): RuntimeTool | undefined {

        return this.tools.get(name);
    }


    list(): RuntimeTool[] {

        return [
            ...this.tools.values()
        ];
    }
}
