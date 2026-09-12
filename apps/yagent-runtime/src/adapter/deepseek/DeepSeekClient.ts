import OpenAI from 'openai';

import {
    ChatCompletionMessageParam,
    ChatCompletionTool
} from 'openai/resources/chat/completions';

import {
    DeepSeekAssistantResult
} from './DeepSeekTypes.js';


export class DeepSeekClient {

    private readonly client: OpenAI;

    private readonly model: string;


    constructor() {

        const apiKey =
            process.env.DEEPSEEK_API_KEY;


        if (!apiKey) {

            throw new Error(
                '缺少环境变量 DEEPSEEK_API_KEY'
            );
        }


        this.client =
            new OpenAI({

                apiKey,

                baseURL:
                    process.env.DEEPSEEK_BASE_URL
                    ?? 'https://api.deepseek.com'
            });


        this.model =
            process.env.DEEPSEEK_MODEL
            ?? 'deepseek-v4-flash';
    }


    async chat(
        messages: ChatCompletionMessageParam[],
        tools?: ChatCompletionTool[]
    ): Promise<DeepSeekAssistantResult> {

        const response =
            await this.client.chat.completions.create({

                model: this.model,

                messages,

                tools:
                    tools && tools.length > 0
                        ? tools
                        : undefined,

                tool_choice:
                    tools && tools.length > 0
                        ? 'auto'
                        : undefined
            });


        const message =
            response.choices[0]?.message;


        if (!message) {

            throw new Error(
                'DeepSeek没有返回有效消息'
            );
        }


        return {

            content:
                message.content ?? null,

            toolCalls:
                message.tool_calls?.map(
                    item => ({

                        id: item.id,

                        type: 'function',

                        function: {

                            name:
                                item.function.name,

                            arguments:
                                item.function.arguments
                        }
                    })
                ) ?? []
        };
    }
}
