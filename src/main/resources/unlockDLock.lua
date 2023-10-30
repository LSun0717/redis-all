-- 查询比较锁的持有者是否是自己，并决定是否释放
if (redis.call('GET', KEYS[1]) == ARGV[1]) then
    return redis.call('DEL', KEYS[1])
end
return 0;