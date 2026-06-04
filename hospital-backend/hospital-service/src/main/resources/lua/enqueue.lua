local member = "queue:" .. ARGV[1]
local signTs = tonumber(ARGV[3])
local N = tonumber(ARGV[4])
local step = tonumber(ARGV[5])
local emergencyOffset = tonumber(ARGV[6])

if ARGV[2] == "EMERGENCY" then
    redis.call("ZADD", KEYS[1], signTs - emergencyOffset, member)

elseif ARGV[2] == "REVISIT" then
    local firsts = redis.call("ZRANGE", KEYS[1], 0, 0, "WITHSCORES")
    if #firsts > 0 then
        local firstScore = tonumber(firsts[2])
        local score = firstScore + N * step
        local existing = redis.call("ZSCORE", KEYS[1], member)
        if existing then score = score + 1 end
        redis.call("ZADD", KEYS[1], score, member)
    else
        redis.call("ZADD", KEYS[1], signTs, member)
    end

else
    redis.call("ZADD", KEYS[1], signTs, member)
end

local rank = redis.call("ZRANK", KEYS[1], member)
return { rank, redis.call("ZCARD", KEYS[1]) }
