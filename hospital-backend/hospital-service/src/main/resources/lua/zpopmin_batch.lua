local results = redis.call("ZPOPMIN", KEYS[1], tonumber(ARGV[1]))
if not results or #results == 0 then return nil end
return results[1]
