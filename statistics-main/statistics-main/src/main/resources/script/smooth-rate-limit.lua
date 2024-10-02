local rate = redis.call('HGET', KEYS[1], KEYS[3]);
local rate_ms = redis.call('HGET', KEYS[1], KEYS[4]);

local interval_ms = 20;
if (rate_ms)
then
    if (tonumber(rate_ms) > 0) then
        rate_ms = tonumber(rate_ms)
    end
else
    rate_ms = 1000
end
if (rate)
then
    if (tonumber(rate) > 0)
    then
        interval_ms = rate_ms / tonumber(rate)
    end
end

local wait_ms = 0;
local val = redis.call('HGET', KEYS[1], KEYS[2]);
if (val)
then
    val = tonumber(val)
    local diff = tonumber(ARGV[1]) - tonumber(interval_ms);
    if (diff < val) then
        wait_ms = val - diff
    else
        redis.call('HSET', KEYS[1], KEYS[2], ARGV[1]);
    end
else
    redis.call('HSET', KEYS[1], KEYS[2], ARGV[1]);
end

return wait_ms;
