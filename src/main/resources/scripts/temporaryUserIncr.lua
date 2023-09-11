local key = KEYS[1]
local increment = tonumber(ARGV[1])
local current_value = tonumber(redis.call('GET', key) or 0)
current_value = current_value + increment
redis.call('SET', key, current_value)
return tostring(current_value)