package tech.ada.java.agendamentoconsultas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import tech.ada.java.agendamentoconsultas.model.enums.UserRole;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("is_active = true")
public class Doctor implements UserDetails, AuthenticatedUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "O campo nome não pode ser nulo ou vazio")
    private String name;
    @NotBlank(message = "O campo CRM não pode ser nulo ou vazio")
    private String crm;
    @Email
    @NotBlank(message = "O campo e-mail não pode ser nulo ou vazio")
    private String email;
    @NotBlank(message = "O campo senha não pode ser nulo ou vazio")
    private String password;
    private Boolean isActive = true;
    private String specialty;
    private UUID uuid = UUID.randomUUID();

    private Boolean accountExpired = !isActive;
    private Boolean credentialsExpired = !isActive;
    private Boolean accountLocked = !isActive;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.DOCTOR;

    @ManyToOne
    @JoinColumn(name = "address_id")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Address address;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }


    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !this.accountExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !this.credentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.isActive;
    }
}