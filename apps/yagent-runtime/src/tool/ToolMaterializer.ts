import fs from 'node:fs/promises';
import fssync from 'node:fs';
import path from 'node:path';
import crypto from 'node:crypto';
import AdmZip from 'adm-zip';
import {parseToolManifest} from '@yagent/tool-protocol';
import type {PlatformClient, ResolvedTool} from '../platform/PlatformClient.js';

export interface MaterializedTool {
    packageDir: string;
    entrypoint: string;
    timeoutMs: number;
}

export class ToolMaterializer {
    constructor(private readonly platform: PlatformClient, private readonly cacheRoot: string) {
    }

    async materialize(tool: ResolvedTool): Promise<MaterializedTool> {
        const dir = path.resolve(this.cacheRoot, tool.toolId, tool.version);
        const marker = path.join(dir, '.verified');
        if (fssync.existsSync(marker)) return {packageDir: dir, entrypoint: tool.entrypoint, timeoutMs: tool.timeoutMs};

        await fs.mkdir(path.dirname(dir), {recursive: true});
        const ticket = await this.platform.downloadTicket(tool.toolId, tool.version);
        const response = await fetch(ticket.url);
        if (!response.ok) throw new Error(`Tool package download failed ${response.status}`);
        const data = Buffer.from(await response.arrayBuffer());
        const actual = crypto.createHash('sha256').update(data).digest('hex');
        if (actual.toLowerCase() !== ticket.sha256.toLowerCase()) throw new Error(`Tool SHA256 mismatch: expected ${ticket.sha256}, actual ${actual}`);

        const tmp = dir + `.tmp-${Date.now()}`;
        await fs.rm(tmp, {recursive: true, force: true});
        await fs.mkdir(tmp, {recursive: true});
        const zipPath = path.join(tmp, 'package.ytool');
        await fs.writeFile(zipPath, data);
        new AdmZip(zipPath).extractAllTo(tmp, true);
        await fs.rm(zipPath, {force: true});
        const manifestRaw = JSON.parse(await fs.readFile(path.join(tmp, 'yagent-tool.json'), 'utf8'));
        const manifest = parseToolManifest(manifestRaw);
        if (manifest.id !== tool.toolId || manifest.version !== tool.version) throw new Error('Tool manifest identity/version mismatch');
        const entry = path.resolve(tmp, manifest.runtime.entry);
        if (!(entry === tmp || entry.startsWith(tmp + path.sep)) || !fssync.existsSync(entry)) throw new Error('Invalid tool entrypoint');
        await fs.writeFile(path.join(tmp, '.verified'), JSON.stringify({
            sha256: actual,
            verifiedAt: new Date().toISOString()
        }));
        await fs.rm(dir, {recursive: true, force: true});
        await fs.rename(tmp, dir);
        return {
            packageDir: dir,
            entrypoint: manifest.runtime.entry,
            timeoutMs: manifest.limits?.timeoutMs ?? tool.timeoutMs ?? 10000
        };
    }
}
