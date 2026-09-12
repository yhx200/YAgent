import { defineYagentToolExecutor } from '@yagent/tool-sdk-node';

export const execute = defineYagentToolExecutor(async (toolName, args) => {
  if (toolName !== 'weather.query') throw new Error(`Unsupported tool: ${toolName}`);
  const city = String(args.city ?? '').trim();
  if (!city) throw new Error('city is required');
  return { city, temperature: 26, unit: '°C', condition: '晴', source: 'demo-weather' };
});
