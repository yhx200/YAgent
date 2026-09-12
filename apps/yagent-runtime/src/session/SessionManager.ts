import {
    SessionContext,
    SessionMessage
} from './SessionContext.js';


export class SessionManager {

    private readonly sessions =
        new Map<string, SessionContext>();


    getOrCreate(
        sessionId: string
    ): SessionContext {

        let session =
            this.sessions.get(sessionId);


        if (!session) {

            const now = Date.now();


            session = {

                sessionId,

                createdAt: now,

                updatedAt: now,

                messages: []
            };


            this.sessions.set(
                sessionId,
                session
            );
        }


        return session;
    }


    get(
        sessionId: string
    ): SessionContext | undefined {

        return this.sessions.get(sessionId);
    }


    addMessage(
        sessionId: string,
        message: SessionMessage
    ): void {

        const session =
            this.getOrCreate(sessionId);


        session.messages.push(message);

        session.updatedAt =
            Date.now();
    }


    clear(
        sessionId: string
    ): void {

        this.sessions.delete(sessionId);
    }


    list(): SessionContext[] {

        return [
            ...this.sessions.values()
        ];
    }
}
