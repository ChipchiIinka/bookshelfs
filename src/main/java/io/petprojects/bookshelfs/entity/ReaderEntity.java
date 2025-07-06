package io.petprojects.bookshelfs.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "readers")
public class ReaderEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    @NotNull
    @Column(unique = true)
    private String email;

    @Pattern(regexp = "^(?=[a-zA-Z0-9._]{3,32}$)(?!.*[_.]{2})[^_.].*[^_.]$")
    @NotNull
    @Column(unique = true, length = 20)
    private String username;

    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$")
    @NotNull
    @Column(length = 60)
    private String password;

    @NotNull
    @Column(length = 30)
    private String publicName;

    private int bookCount;

    @OneToMany(mappedBy = "reader", fetch = FetchType.EAGER, orphanRemoval = true)
    private List<BookshelfEntity> bookshelfs;

    private boolean enabled;

    private String verificationCode;

    private LocalDateTime verificationCodeExpiry;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roles = Set.of("USER");

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}
