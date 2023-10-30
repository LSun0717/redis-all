-- 库存判断与重复下单判断

local voucherId = ARG[1]
local userId = ARG[2]

local stockKey = 'flashSale:stock:' .. voucherId
local orderKey = 'flashSale:order' .. voucherId
-- 判断库存是否充足
if (tonumber(redis.call('GET', stockKey)) <= 0) then
    return 1
end
-- 判断是否重复下单
if (redis.call('SISMEMBER', orderKey, userId) == 1) then
    return 2
end
-- 扣减库存
redis.call('INCRBY', stockKey, -1)
redis.call('SADD', orderKey, userId)
return 0