import { randomUUID } from 'node:crypto';
export function newTraceId(): string { return randomUUID().replaceAll('-', ''); }
export function errorMessage(error: unknown): string { return error instanceof Error ? error.message : String(error); }
