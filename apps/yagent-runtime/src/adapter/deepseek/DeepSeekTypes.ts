export interface DeepSeekToolCall {

    id: string;

    type: 'function';

    function: {

        name: string;

        arguments: string;
    };
}


export interface DeepSeekAssistantResult {

    content: string | null;

    toolCalls: DeepSeekToolCall[];
}
