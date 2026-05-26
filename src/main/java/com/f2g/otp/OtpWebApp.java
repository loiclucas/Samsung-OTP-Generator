package com.f2g.otp;

import com.f2g.otp.model.GenerateRequest;
import com.f2g.otp.model.GenerateResponse;
import com.f2g.otp.service.AppConfig;
import com.f2g.otp.service.OtpService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.staticfiles.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;

public class OtpWebApp {
    private static final Logger log = LoggerFactory.getLogger(OtpWebApp.class);

    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        AppConfig appConfig = AppConfig.fromEnv();
        OtpService otpService = new OtpService(appConfig);

        Javalin app = Javalin.create(serverConfig -> {
            serverConfig.staticFiles.add("/public", Location.CLASSPATH);
        }).start(port);

        app.get("/health", ctx -> ctx.json(Map.of("status", "ok")));
        app.post("/api/generate", ctx -> handleGenerate(ctx, appConfig, otpService));

        log.info("OTP web application started on port {} otpEnabled={}", port, appConfig.otpEnabled);
    }

    private static void handleGenerate(Context ctx, AppConfig config, OtpService otpService) {
        if (config.otpEnabled && !isAuthorized(ctx, config.appAuthToken)) {
            ctx.status(HttpStatus.UNAUTHORIZED).json(Map.of("error", "Unauthorized"));
            return;
        }

        GenerateRequest request = ctx.bodyAsClass(GenerateRequest.class);
        String clientIp = ctx.req().getRemoteAddr();

        GenerateResponse response = otpService.generate(request.seed, request.formatHint);

        log.info(
            "clientIp={} keyFormat={} success={} message={}",
            clientIp,
            response.keyFormat,
            response.success,
            response.message
        );

        ctx.json(response);
    }

    private static boolean isAuthorized(Context ctx, String expectedToken) {
        String headerToken = ctx.header("X-App-Auth-Token");
        String authorization = ctx.header("Authorization");
        if ((headerToken == null || headerToken.isBlank()) && authorization != null) {
            String prefix = "Bearer ";
            if (authorization.regionMatches(true, 0, prefix, 0, prefix.length())) {
                headerToken = authorization.substring(prefix.length()).trim();
            }
        }

        if (headerToken == null) {
            return false;
        }

        byte[] expected = expectedToken.getBytes(StandardCharsets.UTF_8);
        byte[] provided = headerToken.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(expected, provided);
    }
}
