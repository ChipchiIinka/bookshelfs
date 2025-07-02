package io.petprojects.bookshelfs.entity.listner;

import io.petprojects.bookshelfs.entity.ReaderEntity;
import jakarta.persistence.PrePersist;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReaderEntityListener {

    private final PasswordEncoder passwordEncoder;

    @PrePersist
    public void prePersist(ReaderEntity user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            log.debug("Encoding password for user: {}", user.getUsername());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
    }
}
