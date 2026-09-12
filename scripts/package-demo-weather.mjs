import fs from 'node:fs';
import path from 'node:path';
import crypto from 'node:crypto';
import AdmZip from 'adm-zip';

const root = process.cwd();
const source = path.join(root, 'tools', 'demo-weather');
const outDir = path.join(root, 'runtime-data', 'tool-store', 'com.yagent.weather', '1.0.0');
fs.mkdirSync(outDir, { recursive: true });
const zip = new AdmZip();
zip.addLocalFile(path.join(source, 'yagent-tool.json'));
zip.addLocalFile(path.join(source, 'package.json'));
zip.addLocalFolder(path.join(source, 'dist'), 'dist');
const out = path.join(outDir, 'package.ytool');
zip.writeZip(out);
const sha = crypto.createHash('sha256').update(fs.readFileSync(out)).digest('hex');
console.log(`created: ${out}`);
console.log(`sha256 : ${sha}`);
console.log('把该 SHA256 同步到 ya_tool_version.package_sha256');
