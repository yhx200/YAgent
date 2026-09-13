import {Router} from 'express';
import {z} from 'zod';
import type {AgentService} from '../agent/AgentService.js';
import type {SessionManager} from '../session/SessionManager.js';

const Chat = z.object({
    tenantId: z.number().int().positive(),
    userId: z.number().int().positive(),
    sessionId: z.string().min(1),
    message: z.string().min(1)
});

export function createRuntimeRouter(agent: AgentService, sessions: SessionManager) {
    const r = Router();
    r.get('/health', (_req, res) => res.json({
        status: 'UP',
        service: 'yagent-runtime',
        time: new Date().toISOString()
    }));
    r.post('/chat', async (req, res) => {
        try {
            const body = Chat.parse(req.body);
            const reply = await agent.chat({
                tenantId: body.tenantId,
                userId: body.userId,
                sessionId: body.sessionId
            }, body.message);
            res.json({success: true, data: {sessionId: body.sessionId, reply}});
        } catch (e) {
            console.error(e);
            res.status(500).json({success: false, message: e instanceof Error ? e.message : String(e)});
        }
    });
    r.get('/sessions/:id', (req, res) => {
        const s = sessions.get(req.params.id);
        if (!s) {
            res.status(404).json({success: false, message: 'Session not found'});
            return;
        }
        res.json({success: true, data: s});
    });
    r.delete('/sessions/:id', (req, res) => {
        sessions.clear(req.params.id);
        res.json({success: true});
    });
    return r;
}
