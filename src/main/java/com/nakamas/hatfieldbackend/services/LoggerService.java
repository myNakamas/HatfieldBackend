package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.models.entities.Log;
import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.UsedPart;
import com.nakamas.hatfieldbackend.models.views.incoming.filters.LogFilter;
import com.nakamas.hatfieldbackend.models.views.outgoing.LogView;
import com.nakamas.hatfieldbackend.models.views.outgoing.PageView;
import com.nakamas.hatfieldbackend.models.views.outgoing.user.UserProfile;
import com.nakamas.hatfieldbackend.repositories.LogRepository;
import com.nakamas.hatfieldbackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoggerService {
    private final LogRepository logRepository;
    private final UserRepository userRepository;

    public void createLog(Log logMessage) {
        logMessage.setTimestamp(LocalDateTime.now());
        logRepository.save(logMessage);
        log.info("User '%s' performed: '%s'.".formatted(logMessage.getUserId(), logMessage.getAction()));
    }

    public void createLog(String message, UUID user, Long ticketId) {
        createLog(Log.builder()
                .action(message)
                .userId(user)
                .ticketId(ticketId)
                .build());
    }

    public PageView<LogView> getLogs(LogFilter filter, PageRequest pageRequest) {
        Page<Log> all = logRepository.findAll(filter, pageRequest);
        return new PageView<>(all.map((log) -> new LogView(log, getUserProfile(log.getUserId()))));
    }

    private UserProfile getUserProfile(UUID id) {
        return userRepository.findById(id).map(UserProfile::new).orElse(null);
    }

    public void createLogUsedItem(UsedPart usedPart, User user) {
        Log build = Log.builder()
                .action("User '%s' has used part '%s' for Ticket#%s ".formatted(user.getFullName(), usedPart.getItem(), usedPart.getTicket().getId()))
                .userId(user.getId())
                .ticketId(usedPart.getTicket().getId())
                .partUsedId(usedPart.getId())
                .build();
        createLog(build);
    }
}
