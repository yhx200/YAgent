CREATE TABLE ya_capability (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    capability_code VARCHAR(128) NOT NULL,
    name VARCHAR(128) NOT NULL,
    description TEXT,
    category VARCHAR(64),
    keywords VARCHAR(1000),
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    UNIQUE KEY uk_capability_code (capability_code)
);
CREATE TABLE ya_tool (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tool_id VARCHAR(128) NOT NULL,
    name VARCHAR(128) NOT NULL,
    display_name VARCHAR(255),
    description TEXT,
    publisher_id BIGINT,
    status VARCHAR(32),
    create_time DATETIME,
    update_time DATETIME,
    UNIQUE KEY uk_tool_id(tool_id)
);
CREATE TABLE ya_tool_version (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tool_id VARCHAR(128) NOT NULL,
    version VARCHAR(32) NOT NULL,
    protocol_version VARCHAR(16),
    runtime_type VARCHAR(32),
    manifest_json LONGTEXT,
    package_object_key VARCHAR(500),
    package_sha256 VARCHAR(128),
    signature TEXT,
    status VARCHAR(32),
    create_time DATETIME,
    UNIQUE KEY uk_tool_version(tool_id, version)
);
CREATE TABLE ya_tool_capability (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tool_id VARCHAR(128) NOT NULL,
    tool_version VARCHAR(32) NOT NULL,
    capability_code VARCHAR(128) NOT NULL,
    tool_name VARCHAR(128) NOT NULL,
    priority INT DEFAULT 100
);
CREATE TABLE ya_tool_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tool_id VARCHAR(128) NOT NULL,
    version VARCHAR(32) NOT NULL,
    permission_type VARCHAR(64),
    permission_value TEXT
);
CREATE TABLE ya_tenant_installation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    tool_id VARCHAR(128) NOT NULL,
    version VARCHAR(32) NOT NULL,
    status VARCHAR(32),
    install_user_id BIGINT,
    install_time DATETIME,
    UNIQUE KEY uk_tenant_tool(tenant_id,tool_id)
);
CREATE TABLE ya_tenant_tool_policy (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    tool_id VARCHAR(128),
    policy_type VARCHAR(64),
    policy_value TEXT,
    status VARCHAR(32)
);
CREATE TABLE ya_audit_event (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    trace_id VARCHAR(64),
    tenant_id BIGINT,
    user_id BIGINT,
    session_id VARCHAR(64),
    event_type VARCHAR(64),
    capability_code VARCHAR(128),
    tool_id VARCHAR(128),
    tool_version VARCHAR(32),
    tool_name VARCHAR(128),
    result_status VARCHAR(32),
    duration_ms BIGINT,
    detail_json LONGTEXT,
    create_time DATETIME
);
