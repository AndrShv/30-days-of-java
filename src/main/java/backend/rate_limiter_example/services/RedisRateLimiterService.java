package backend.rate_limiter_example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisRateLimiterService {

    private final StringRedisTemplate redisTemplate;

    private static final String LUA_RATE_LIMITER_SCRIPT =
            "local key = KEYS[1]\n" +
                    "local limit = tonumber(ARGV[1])\n" +
                    "local window = tonumber(ARGV[2])\n" +
                    "local current = redis.call('INCR', key)\n" +
                    "if current == 1 then\n" +
                    "    redis.call('EXPIRE', key, window)\n" +
                    "end\n" +
                    "if current > limit then\n" +
                    "    return 0\n" +
                    "else\n" +
                    "    return 1\n" +
                    "end";

    private static final DefaultRedisScript<Long> REDIS_SCRIPT;

    static {
        REDIS_SCRIPT = new DefaultRedisScript<>();
        REDIS_SCRIPT.setScriptText(LUA_RATE_LIMITER_SCRIPT);
        REDIS_SCRIPT.setResultType(Long.class);
    }

    public boolean isAllowed(String userId, int limit, int windowInSeconds) {
        String key = "rate_limit:" + userId;

        Long result = redisTemplate.execute(
                REDIS_SCRIPT,
                Collections.singletonList(key),
                String.valueOf(limit),
                String.valueOf(windowInSeconds)
        );

        return result == 1L;
    }
}