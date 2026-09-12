import express from 'express';
import { z } from 'zod';
import { spawn } from 'node:child_process';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const app = express();
app.use(express.json({ limit: '2mb' }));

const InvokeSchema = z.object({
  requestId: z.string(),
  packageDir: z.string(),
  entrypoint: z.string(),
  toolName: z.string(),
  arguments: z.record(z.string(), z.unknown()),
  timeoutMs: z.number().int().positive().max(60000).default(10000)
});

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const workerPath = path.join(__dirname, 'worker.js');

app.get('/health', (_req, res) => res.json({ status: 'UP', service: 'node-runner' }));

app.post('/invoke', async (req, res) => {
  let request: z.infer<typeof InvokeSchema>;
  try { request = InvokeSchema.parse(req.body); }
  catch (e) { res.status(400).json({ success: false, error: String(e) }); return; }

  const child = spawn(process.execPath, [workerPath], {
    stdio: ['pipe', 'pipe', 'pipe'],
    env: { ...process.env, YAGENT_RUNNER_TOOL_ROOT: process.env.YAGENT_RUNNER_TOOL_ROOT ?? '/data/tool-cache' }
  });

  let stdout = '';
  let stderr = '';
  child.stdout.on('data', d => stdout += d.toString());
  child.stderr.on('data', d => stderr += d.toString());
  child.stdin.end(JSON.stringify(request));

  const timer = setTimeout(() => child.kill('SIGKILL'), request.timeoutMs);
  child.on('close', code => {
    clearTimeout(timer);
    if (code !== 0) {
      res.status(500).json({ requestId: request.requestId, success: false, error: stderr || `worker exited ${code}` });
      return;
    }
    try { res.json(JSON.parse(stdout)); }
    catch { res.status(500).json({ requestId: request.requestId, success: false, error: `invalid worker response: ${stdout}` }); }
  });
});

const port = Number(process.env.PORT ?? '8090');
app.listen(port, () => console.log(`[node-runner] listening on ${port}`));
