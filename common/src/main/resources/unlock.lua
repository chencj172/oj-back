--- 当前线程标识和redis中记录的线程标识一致才进行释放锁
if (redis.call('get', KEYS[1]) == ARGV[1]) then
    return redis.call('del', KEYS[1])
end
return 0;