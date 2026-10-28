package backend.rate_limiter.aspects;



import backend.rate_limiter.annotations.RateLimit;
import backend.rate_limiter.exceptions.TooManyRequestsException;
import backend.rate_limiter.services.RedisRateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component("customRedisRateLimiterAspect")
@RequiredArgsConstructor
public class RateLimiterAspect {

    private final RedisRateLimiterService rateLimiterService;

    @Before("@annotation(rateLimit)")
    public void checkRateLimit(JoinPoint joinPoint, RateLimit rateLimit) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return;

        HttpServletRequest request = attributes.getRequest();

        String userId = request.getHeader("X-User-Id");
        if (userId == null || userId.isBlank()) {
            userId = request.getRemoteAddr();
        }

        boolean allowed = rateLimiterService.isAllowed(userId, rateLimit.limit(), rateLimit.windowInSeconds());

        if (!allowed) {
            log.warn("Rate limit exceeded for user: {}. Limit: {} req/{}s", userId, rateLimit.limit(), rateLimit.windowInSeconds());
            throw new TooManyRequestsException("Rate limit exceeded. Maximum " + rateLimit.limit() + " requests per second allowed.");
        }
    }
}
