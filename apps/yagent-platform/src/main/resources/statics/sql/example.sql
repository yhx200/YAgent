INSERT INTO ya_tool
(
    tool_id,
    tool_name,
    description,
    status
)
VALUES
    (
        'com.yagent.weather',
        'Demo Weather',
        'YAgent Demo Weather Tool',
        'ENABLED'
    );
INSERT INTO ya_capability
(
    capability_code,
    capability_name,
    description,
    keywords,
    provider_type,
    tool_id,
    status
)
VALUES
    (
        'weather.forecast',
        '天气查询',
        '查询城市天气和天气预报',
        '天气,温度,天气预报,下雨,晴天,气温',
        'TOOL',
        'com.yagent.weather',
        'ENABLED'
    );
INSERT INTO ya_tool_version
(
    tool_id,
    version,
    manifest_json,
    bucket,
    object_key,
    sha256,
    signature,
    status
)
VALUES
    (
        'com.yagent.weather',
        '1.0.0',

        '{
            "id":"com.yagent.weather",
            "name":"Demo Weather",
            "version":"1.0.0",
            "entry":"dist/index.js",
            "tools":[
                {
                    "name":"weather.query",
                    "description":"查询天气"
                }
            ]
        }',

        'yagent-tools',

        'com.yagent.weather/1.0.0/package.ytool',

        '这里换成真实SHA256',

        NULL,

        'PUBLISHED'
    );
INSERT INTO ya_tool_installation
(
    tenant_id,
    tool_id,
    version,
    status
)
VALUES
    (
        10001,
        'com.yagent.weather',
        '1.0.0',
        'ENABLED'
    );
