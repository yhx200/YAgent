import fs from 'node:fs';
import path from 'node:path';
import { pathToFileURL } from 'node:url';

let raw='';
for await (const chunk of process.stdin) raw += chunk.toString();
const request = JSON.parse(raw);

const root = path.resolve(process.env.YAGENT_RUNNER_TOOL_ROOT ?? '/data/tool-cache');
const packageDir = path.resolve(request.packageDir);
if (!(packageDir === root || packageDir.startsWith(root + path.sep))) {
  console.error('packageDir escapes runner tool root');
  process.exit(2);
}
const entry = path.resolve(packageDir, request.entrypoint);
if (!(entry === packageDir || entry.startsWith(packageDir + path.sep))) {
  console.error('entrypoint escapes package directory');
  process.exit(2);
}
if (!fs.existsSync(entry)) {
  console.error(`entrypoint not found: ${entry}`);
  process.exit(2);
}

try {
  const module = await import(pathToFileURL(entry).href + `?run=${Date.now()}`);
  if (typeof module.execute !== 'function') throw new Error('Tool package must export async function execute(toolName,args)');
  const result = await module.execute(request.toolName, request.arguments);
  process.stdout.write(JSON.stringify({ requestId: request.requestId, success: true, result }));
} catch (error) {
  console.error(error instanceof Error ? error.stack ?? error.message : String(error));
  process.exit(1);
}
