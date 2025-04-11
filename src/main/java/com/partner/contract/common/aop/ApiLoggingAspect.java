package com.partner.contract.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Aspect // AOP
@Component
@Slf4j
public class ApiLoggingAspect {

    private static final long SLOW_API_THRESHOLD = 2000L;

    @Value("${discord.webhook.url}")
    private String DISCORD_WEBHOOK_URL;

    // 요청마다 로그 저장을 위한 ThreadLocal
    private static final ThreadLocal<StringBuilder> logHolder = ThreadLocal.withInitial(StringBuilder::new);

    // 모든 API 감지
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {}

    // 계층별 메서드 감지
    @Pointcut("execution(* com.partner.contract..controller..*(..))")
    public void controllerLayer() {}

    @Pointcut("execution(* com.partner.contract..service..*(..))")
    public void serviceLayer() {}

    @Pointcut("execution(* com.partner.contract..repository..*(..))")
    public void repositoryLayer() {}

    // API 전체 진입/종료 시간 측정 및 디스코드 전송
    @Around("controllerMethods()")
    public Object logApi(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        // API 메서드 이름 가져오기
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String apiName = method.getDeclaringClass().getSimpleName() + "." + method.getName();

        // 로그 저장
        StringBuilder sb = logHolder.get();
        sb.append(String.format("### 🚑 Slow API: %s에서 문제 발생 !!\n", apiName));
        sb.append("```\n");
        // 실제 메서드 실행
        Object result = joinPoint.proceed();

        // 실행 시간 계산
        long duration = System.currentTimeMillis() - start;
        sb.append("```");
        sb.append(String.format("실행 시간이 총 **`%s ms`** 걸렸어요\n", duration));

        // 콘솔 출력
        log.info("\n" + sb);

        // 느린 API라면 디스코드 전송
        if (duration > SLOW_API_THRESHOLD) {
            sendDiscordAlert(sb.toString());
        }

        logHolder.remove(); // ThreadLocal 클리어
        return result;
    }

    // 각 계층별 실행 시간 측정
    @Around("controllerLayer() || serviceLayer() || repositoryLayer()")
    public Object logLayerExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();     // 실제 로직 실행
        long duration = System.currentTimeMillis() - start;

        // 어느 계층인지 추출
        String layer = getLayer(joinPoint);
        String methodName = joinPoint.getSignature().toShortString();

        // 로그 문자열 추가
        String logLine = String.format("[%s]\n%s에서 %d ms 걸렸어요!\n\n", layer, methodName, duration);
        logHolder.get().append(logLine);

        return result;
    }

    // 클래스 이름으로부터 Controller / Service / Repository 계층 이름 판별
    private String getLayer(ProceedingJoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        if (className.contains(".controller")) return "Controller";
        if (className.contains(".service")) return "Service";
        if (className.contains(".repository")) return "Repository";
        return "Unknown";
    }

    // 디스코드 Webhook으로 메시지 전송
    private void sendDiscordAlert(String message) {
        try {
            // JSON payload 구성
            String payload = "{\"content\": \"" + escapeJson(message) + "\"}";

            // HTTP POST 요청 구성
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(DISCORD_WEBHOOK_URL))
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .header("Content-Type", "application/json")
                    .build();

            // 비동기 전송
            HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.discarding());
        } catch (Exception e) {
            log.error("❌ Failed to send Discord webhook", e);
        }
    }

    // JSON 문자열 안전하게 이스케이프 처리
    private String escapeJson(String message) {
        return message.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }
}
