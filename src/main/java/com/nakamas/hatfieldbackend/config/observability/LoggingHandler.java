package com.nakamas.hatfieldbackend.config.observability;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class LoggingHandler implements ObservationHandler<Observation.Context> {


    @Override
    public boolean supportsContext(@NonNull Observation.Context context) {
        return true;
    }

    @Override
    public void onStart(@NonNull Observation.Context context) {
        if (isNonLegacyScope(context)) {
            log.info("Starting " + context.getName());
        }
        context.put("time", System.currentTimeMillis());
    }

    @Override
    public void onScopeOpened(@NonNull Observation.Context context) {
        if (isNonLegacyScope(context))
            log.info("Scope opened  " + context.getName());
    }

    @Override
    public void onScopeClosed(@NonNull Observation.Context context) {
        if (isNonLegacyScope(context))
            log.info("Scope closed " + context.getName());
    }

    @Override
    public void onStop(@NonNull Observation.Context context) {
        String message = "Stopping "
                + context.getName()
                + " duration "
                + (System.currentTimeMillis() - context.getOrDefault("time", 0L));
        if (isNonLegacyScope(context))
            log.info(message);
        else log.debug(message);

    }

    @Override
    public void onError(Observation.Context context) {
        log.error("Error " + Objects.requireNonNull(context.getError()).getMessage());
    }

    private static boolean isNonLegacyScope(Observation.Context context) {
        return !(context.getName().contains("spring") || context.getName().equals("http.server.requests"));
    }
}