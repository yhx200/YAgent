package com.yagent.platform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yagent.platform.domain.*;
import com.yagent.platform.dto.RuntimeDtos.*;
import com.yagent.platform.dto.ToolManifest;
import com.yagent.platform.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.*;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class RuntimePlatformService {
  private final YaCapabilityMapper capabilityMapper;
  private final YaToolCapabilityMapper toolCapabilityMapper;
  private final YaToolMapper toolMapper;
  private final YaToolVersionMapper toolVersionMapper;
  private final YaToolPermissionMapper toolPermissionMapper;
  private final YaTenantInstallationMapper installationMapper;
  private final YaTenantToolPolicyMapper policyMapper;
  private final YaAuditEventMapper auditMapper;
  private final ObjectMapper objectMapper;

  public CapabilitySearchResponse search(CapabilitySearchRequest request){
    List<YaCapability> capabilities=capabilityMapper.selectActive();
    List<Scored> scored=new ArrayList<>();
    for(YaCapability c:capabilities){double score=score(request.getQuery(),c);if(score>0)scored.add(new Scored(c,score));}
    scored.sort(Comparator.comparingDouble(Scored::score).reversed());
    int limit=Math.min(Math.max(Optional.ofNullable(request.getLimit()).orElse(5),1),20);
    List<Scored> top=scored.stream().limit(limit).toList();
    List<String> codes=top.stream().map(x->x.capability().getCapabilityCode()).toList();
    Map<String,List<YaToolCapability>> mapping=codes.isEmpty()?Map.of():toolCapabilityMapper.selectByCapabilityCodes(codes).stream().collect(Collectors.groupingBy(YaToolCapability::getCapabilityCode));
    CapabilitySearchResponse response=new CapabilitySearchResponse();
    for(Scored s:top){
      CapabilityItem item=new CapabilityItem(); YaCapability c=s.capability(); item.setCapabilityCode(c.getCapabilityCode());item.setName(c.getName());item.setDescription(c.getDescription());item.setCategory(c.getCategory());item.setScore(s.score());
      for(YaToolCapability m:mapping.getOrDefault(c.getCapabilityCode(),List.of())){
        CapabilityProvider p=new CapabilityProvider();p.setType("TOOL");p.setToolId(m.getToolId());p.setVersion(m.getToolVersion());p.setToolName(m.getToolName());p.setInstalled(installationMapper.selectOneInstalled(request.getTenantId(),m.getToolId())!=null);item.getProviders().add(p);
      }
      response.getItems().add(item);
    }
    return response;
  }

  public InstallationResolveResponse resolve(InstallationResolveRequest request){
    InstallationResolveResponse response=new InstallationResolveResponse();response.setCapabilityCode(request.getCapabilityCode());
    List<YaToolCapability> mappings=toolCapabilityMapper.selectByCapabilityCode(request.getCapabilityCode());
    if(mappings.isEmpty())return response;
    List<String> ids=mappings.stream().map(YaToolCapability::getToolId).distinct().toList();
    Map<String,YaTool> tools=toolMapper.selectActiveByIds(ids).stream().collect(Collectors.toMap(YaTool::getToolId,x->x));
    for(YaToolCapability mapping:mappings){
      YaTenantInstallation install=installationMapper.selectOneInstalled(request.getTenantId(),mapping.getToolId());
      if(install==null||!Objects.equals(install.getVersion(),mapping.getToolVersion()))continue;
      YaTenantToolPolicy policy=policyMapper.selectAccessPolicy(request.getTenantId(),mapping.getToolId());
      if(policy!=null&&"DENY".equalsIgnoreCase(policy.getPolicyValue()))continue;
      YaTool tool=tools.get(mapping.getToolId());if(tool==null)continue;
      YaToolVersion version=toolVersionMapper.selectPublished(mapping.getToolId(),install.getVersion());if(version==null)continue;
      ToolManifest manifest=parseManifest(version.getManifestJson());
      ToolManifest.ToolFunction fn=manifest.getTools().stream().filter(x->Objects.equals(x.getName(),mapping.getToolName())).findFirst().orElse(null);if(fn==null)continue;
      ResolvedTool dto=new ResolvedTool();dto.setToolId(tool.getToolId());dto.setVersion(version.getVersion());dto.setToolName(mapping.getToolName());dto.setDisplayName(tool.getDisplayName());dto.setDescription(StringUtils.hasText(fn.getDescription())?fn.getDescription():tool.getDescription());dto.setRuntimeType(version.getRuntimeType());dto.setProtocolVersion(version.getProtocolVersion());dto.setEntrypoint(manifest.getRuntime().getEntry());dto.setInputSchema(fn.getInputSchema());dto.setTimeoutMs(manifest.getLimits()!=null&&manifest.getLimits().getTimeoutMs()!=null?manifest.getLimits().getTimeoutMs():10000);
      PackageInfo pkg=new PackageInfo();pkg.setObjectKey(version.getPackageObjectKey());pkg.setSha256(version.getPackageSha256());pkg.setSignature(version.getSignature());dto.setPackageInfo(pkg);
      for(YaToolPermission permission:toolPermissionMapper.selectByToolVersion(tool.getToolId(),version.getVersion())){ToolPermission p=new ToolPermission();p.setType(permission.getPermissionType());p.setValue(permission.getPermissionValue());dto.getPermissions().add(p);}
      response.getTools().add(dto);
    }
    return response;
  }

  public PermissionEvaluateResponse evaluate(PermissionEvaluateRequest request){
    PermissionEvaluateResponse response=new PermissionEvaluateResponse();
    if(installationMapper.selectOneInstalled(request.getTenantId(),request.getToolId())==null){response.setDecision("DENY");response.getReasons().add("Tool is not installed for tenant");return response;}
    YaTenantToolPolicy policy=policyMapper.selectAccessPolicy(request.getTenantId(),request.getToolId());
    if(policy!=null&&"DENY".equalsIgnoreCase(policy.getPolicyValue())){response.setDecision("DENY");response.getReasons().add("Tenant policy denies this tool");return response;}
    List<YaToolPermission> permissions=toolPermissionMapper.selectByToolVersion(request.getToolId(),request.getVersion());
    boolean dangerous=permissions.stream().anyMatch(p->"SHELL".equalsIgnoreCase(p.getPermissionType())&&"true".equalsIgnoreCase(p.getPermissionValue()));
    if(dangerous){response.setDecision("REQUIRE_APPROVAL");response.getReasons().add("Tool requests shell permission");return response;}
    response.setDecision("ALLOW");return response;
  }

  public void audit(AuditEventRequest request){YaAuditEvent e=new YaAuditEvent();e.setTraceId(request.getTraceId());e.setTenantId(request.getTenantId());e.setUserId(request.getUserId());e.setSessionId(request.getSessionId());e.setEventType(request.getEventType());e.setCapabilityCode(request.getCapabilityCode());e.setToolId(request.getToolId());e.setToolVersion(request.getToolVersion());e.setToolName(request.getToolName());e.setResultStatus(request.getResultStatus());e.setDurationMs(request.getDurationMs());e.setDetailJson(request.getDetailJson());auditMapper.insert(e);}
  public YaToolVersion version(String toolId,String version){return toolVersionMapper.selectPublished(toolId,version);}

  private ToolManifest parseManifest(String json){try{return objectMapper.readValue(json,ToolManifest.class);}catch(Exception e){throw new IllegalStateException("Invalid manifest_json",e);}}
  private double score(String query,YaCapability c){String q=normalize(query);double s=0;if(contains(q,c.getName()))s+=5;if(contains(q,c.getDescription()))s+=2;if(StringUtils.hasText(c.getKeywords()))for(String k:c.getKeywords().split("[,，]"))if(StringUtils.hasText(k)&&q.contains(normalize(k)))s+=3;return s;}
  private String normalize(String s){return s==null?"":s.trim().toLowerCase(Locale.ROOT);}
  private boolean contains(String q,String v){return StringUtils.hasText(v)&&q.contains(normalize(v));}
  private record Scored(YaCapability capability,double score){}
}
