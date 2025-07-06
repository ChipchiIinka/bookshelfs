package io.petprojects.bookshelfs.service.secure;

import io.petprojects.bookshelfs.entity.ReaderEntity;
import io.petprojects.bookshelfs.repository.ReaderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReaderDetailsService implements UserDetailsService {

    private final ReaderRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ReaderEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        log.info("Loading user: {}", user.getUsername());
        return user;
    }
}
