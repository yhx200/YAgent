CREATE TABLE IF NOT EXISTS ya_capability
(
    id
    BIGINT
    PRIMARY
    KEY
    AUTO_INCREMENT,
    capability_code
    VARCHAR
(
    128
) NOT NULL, name VARCHAR
(
    128
) NOT NULL, description TEXT, category VARCHAR
(
    64
), keywords VARCHAR
(
    1000
), status VARCHAR
(
    32
) NOT NULL DEFAULT 'ACTIVE', create_time DATETIME NOT NULL, update_time DATETIME NOT NULL, UNIQUE KEY uk_capability_code
(
    capability_code
)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS ya_tool
(
    id
    BIGINT
    PRIMARY
    KEY
    AUTO_INCREMENT,
    tool_id
    VARCHAR
(
    128
) NOT NULL, name VARCHAR
(
    128
) NOT NULL, display_name VARCHAR
(
    255
), description TEXT, publisher_id BIGINT, status VARCHAR
(
    32
), create_time DATETIME, update_time DATETIME, UNIQUE KEY uk_tool_id
(
    tool_id
)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS ya_tool_version
(
    id
    BIGINT
    PRIMARY
    KEY
    AUTO_INCREMENT,
    tool_id
    VARCHAR
(
    128
) NOT NULL, version VARCHAR
(
    32
) NOT NULL, protocol_version VARCHAR
(
    16
), runtime_type VARCHAR
(
    32
), manifest_json LONGTEXT, package_object_key VARCHAR
(
    500
), package_sha256 VARCHAR
(
    128
), signature TEXT, status VARCHAR
(
    32
), create_time DATETIME, UNIQUE KEY uk_tool_version
(
    tool_id,
    version
)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS ya_tool_capability
(
    id
    BIGINT
    PRIMARY
    KEY
    AUTO_INCREMENT,
    tool_id
    VARCHAR
(
    128
) NOT NULL, tool_version VARCHAR
(
    32
) NOT NULL, capability_code VARCHAR
(
    128
) NOT NULL, tool_name VARCHAR
(
    128
) NOT NULL, priority INT DEFAULT 100, KEY idx_capability_code
(
    capability_code
)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS ya_tool_permission
(
    id
    BIGINT
    PRIMARY
    KEY
    AUTO_INCREMENT,
    tool_id
    VARCHAR
(
    128
) NOT NULL, version VARCHAR
(
    32
) NOT NULL, permission_type VARCHAR
(
    64
), permission_value TEXT
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS ya_tenant_installation
(
    id
    BIGINT
    PRIMARY
    KEY
    AUTO_INCREMENT,
    tenant_id
    BIGINT
    NOT
    NULL,
    tool_id
    VARCHAR
(
    128
) NOT NULL, version VARCHAR
(
    32
) NOT NULL, status VARCHAR
(
    32
), install_user_id BIGINT, install_time DATETIME, UNIQUE KEY uk_tenant_tool
(
    tenant_id,
    tool_id
)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS ya_tenant_tool_policy
(
    id
    BIGINT
    PRIMARY
    KEY
    AUTO_INCREMENT,
    tenant_id
    BIGINT
    NOT
    NULL,
    tool_id
    VARCHAR
(
    128
), policy_type VARCHAR
(
    64
), policy_value TEXT, status VARCHAR
(
    32
), KEY idx_tenant_tool
(
    tenant_id,
    tool_id
)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS ya_audit_event
(
    id
    BIGINT
    PRIMARY
    KEY
    AUTO_INCREMENT,
    trace_id
    VARCHAR
(
    64
), tenant_id BIGINT, user_id BIGINT, session_id VARCHAR
(
    64
), event_type VARCHAR
(
    64
), capability_code VARCHAR
(
    128
), tool_id VARCHAR
(
    128
), tool_version VARCHAR
(
    32
), tool_name VARCHAR
(
    128
), result_status VARCHAR
(
    32
), duration_ms BIGINT, detail_json LONGTEXT, create_time DATETIME, KEY idx_audit_trace
(
    trace_id
), KEY idx_audit_tenant_time
(
    tenant_id,
    create_time
)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT
IGNORE INTO ya_capability(capability_code,name,description,category,keywords,status,create_time,update_time) VALUES
('weather.current','天气查询','查询指定城市当前天气','WEATHER','天气,气温,温度,晴天,下雨,weather','ACTIVE',NOW(),NOW());
INSERT
IGNORE INTO ya_tool(tool_id,name,display_name,description,publisher_id,status,create_time,update_time) VALUES
('com.yagent.weather','weather','天气查询','YAgent 演示天气 Tool',1,'ACTIVE',NOW(),NOW());
INSERT
IGNORE INTO ya_tool_version(tool_id,version,protocol_version,runtime_type,manifest_json,package_object_key,package_sha256,signature,status,create_time) VALUES
('com.yagent.weather','1.0.0','1.0','NODE','{"schemaVersion":"1.0","id":"com.yagent.weather","version":"1.0.0","name":"weather","displayName":"天气查询","description":"YAgent 演示天气工具","publisher":{"id":"yagent-official","name":"YAgent"},"runtime":{"type":"node","entry":"dist/index.js"},"capabilities":[{"code":"weather.current","tool":"weather.query"}],"tools":[{"name":"weather.query","description":"查询指定城市当前天气","inputSchema":{"type":"object","properties":{"city":{"type":"string","description":"城市名称"}},"required":["city"],"additionalProperties":false},"outputSchema":{"type":"object"}}],"permissions":{"network":{"enabled":false,"allow":[]},"filesystem":{"read":[],"write":[]},"shell":false},"limits":{"timeoutMs":5000,"memoryMb":128}}','com.yagent.weather/1.0.0/package.ytool','7fbe22b9c7e92b6b82cc7561e77b096a93467531cf747405ceda9352ea293d50','DEV-UNSIGNED','PUBLISHED',NOW());
INSERT INTO ya_tool_capability(tool_id, tool_version, capability_code, tool_name, priority)
SELECT 'com.yagent.weather',
       '1.0.0',
       'weather.current',
       'weather.query',
       10 WHERE NOT EXISTS(SELECT 1 FROM ya_tool_capability WHERE tool_id='com.yagent.weather' AND tool_version='1.0.0' AND capability_code='weather.current' AND tool_name='weather.query');
INSERT
IGNORE INTO ya_tenant_installation(tenant_id,tool_id,version,status,install_user_id,install_time) VALUES(10001,'com.yagent.weather','1.0.0','INSTALLED',20001,NOW());
INSERT INTO ya_tool_permission(tool_id, version, permission_type, permission_value)
SELECT 'com.yagent.weather',
       '1.0.0',
       'NETWORK',
       '{"enabled":false,"allow":[]}' WHERE NOT EXISTS(SELECT 1 FROM ya_tool_permission WHERE tool_id='com.yagent.weather' AND version='1.0.0' AND permission_type='NETWORK');
