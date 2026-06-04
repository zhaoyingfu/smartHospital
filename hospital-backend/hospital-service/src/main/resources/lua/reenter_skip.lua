local member = "queue:" .. ARGV[1]
local firsts = redis.call("ZRANGE", KEYS[1], 0, 0, "WITHSCORES")
if #firsts == 0 then
    redis.call("ZADD", KEYS[1], tonumber(ARGV[3]), member)
else
    local firstScore = tonumber(firsts[2])
    local step = tonumber(ARGV[4])
    local score = firstScore + tonumber(ARGV[2]) * step
    redis.call("ZADD", KEYS[1], score, member)
end
return redis.call("ZRANK", KEYS[1], member)
