import type { AgentRuntimeAdapter, AgentStepInput, AgentStepResult } from '@yagent/runtime-core';
let seq=0;
function call(name:string,args:unknown):AgentStepResult{return {content:null,toolCalls:[{id:`mock-${++seq}`,name,arguments:JSON.stringify(args)}]};}
export class MockAgentRuntimeAdapter implements AgentRuntimeAdapter {
  async runStep(input:AgentStepInput):Promise<AgentStepResult>{
    const last=input.messages[input.messages.length-1];
    if(last?.role==='user'){
      const q=last.content??'';
      if(/天气|weather/i.test(q))return call('capability_search',{query:q});
      return {content:`Mock 模式收到：${q}`,toolCalls:[]};
    }
    if(last?.role==='tool'){
      const text=last.content??'{}'; let data:any={}; try{data=JSON.parse(text)}catch{}
      const previousAssistant=[...input.messages].reverse().find(m=>m.role==='assistant'&&m.toolCalls?.length);
      const previousName=previousAssistant?.toolCalls?.[0]?.name;
      if(previousName==='capability_search'){
        const code=data.items?.[0]?.capabilityCode; if(!code)return {content:'没有找到可用能力。',toolCalls:[]};
        return call('capability_acquire',{capabilityCode:code});
      }
      if(previousName==='capability_acquire'){
        const weather=input.tools.find(t=>t.name==='weather_query'); if(!weather)return {content:'能力已获取，但没有发现 weather_query。',toolCalls:[]};
        const user=[...input.messages].reverse().find(m=>m.role==='user')?.content??'';
        const city=(user.match(/(上海|北京|深圳|广州|杭州|重庆|成都|南京|苏州)/)?.[1])??'上海';
        return call(weather.name,{city});
      }
      if(previousName==='weather_query'){
        return {content:`查询完成：${data.city}当前 ${data.temperature}${data.unit??'°C'}，${data.condition}。`,toolCalls:[]};
      }
    }
    return {content:'Mock adapter finished.',toolCalls:[]};
  }
}
