INSERT INTO ya_capability
(
    capability_code,
    name,
    description,
    category,
    keywords,
    status,
    create_time,
    update_time
)
VALUES
    (
        'weather.forecast',
        '天气查询',
        '查询城市天气及天气预报',
        'weather',
        '天气,天气预报,温度,气温,晴天,下雨',
        'ACTIVE',
        NOW(),
        NOW()
    );
INSERT INTO ya_tool
(
    tool_id,
    name,
    display_name,
    description,
    publisher_id,
    status,
    create_time,
    update_time
)
VALUES
    (
        'com.yagent.weather',
        'weather',
        '天气查询工具',
        'Demo Weather Tool',
        1,
        'ACTIVE',
        NOW(),
        NOW()
    );
INSERT INTO ya_tool_version
(
    tool_id,
    version,
    protocol_version,
    runtime_type,
    manifest_json,
    package_object_key,
    package_sha256,
    signature,
    status,
    create_time
)
VALUES
    (
        'com.yagent.weather',
        '1.0.0',
        '1',
        'NODEJS',

        '{
            "id":"com.yagent.weather",
            "name":"Demo Weather",
            "version":"1.0.0",
            "protocolVersion":"1",
            "runtimeType":"NODEJS",
            "entry":"dist/index.js",
            "tools":[
                {
                    "name":"weather.query",
                    "description":"查询天气"
                }
            ]
        }',

        'com.yagent.weather/1.0.0/package.ytool',

        '替换为你的真实SHA256',

        NULL,

        'PUBLISHED',

        NOW()
    );
INSERT INTO ya_tool_capability
(
    tool_id,
    tool_version,
    capability_code,
    tool_name,
    priority
)
VALUES
    (
        'com.yagent.weather',
        '1.0.0',
        'weather.forecast',
        'weather.query',
        100
    );
INSERT INTO ya_tenant_installation
(
    tenant_id,
    tool_id,
    version,
    status,
    install_user_id,
    install_time
)
VALUES
    (
        10001,
        'com.yagent.weather',
        '1.0.0',
        'ACTIVE',
        20001,
        NOW()
    );
