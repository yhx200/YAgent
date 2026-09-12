export type MessageRole =
    'system'
    | 'user'
    | 'assistant'
    | 'tool';


export interface SessionMessage {

    role: MessageRole;

    content: string;

    toolCallId?: string;

    name?: string;
}


export interface SessionContext {

    /**
     * 会话ID
     */
    sessionId: string;


    /**
     * 创建时间
     */
    createdAt: number;


    /**
     * 最后更新时间
     */
    updatedAt: number;


    /**
     * 历史消息
     */
    messages: SessionMessage[];
}
