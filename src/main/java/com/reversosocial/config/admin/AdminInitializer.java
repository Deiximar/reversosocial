package com.reversosocial.config.admin;

import org.springframework.stereotype.Component;
import com.reversosocial.models.entity.Role;
import com.reversosocial.models.entity.User;
import com.reversosocial.models.entity.ERole;
import com.reversosocial.repository.UserRepository;
import com.reversosocial.repository.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Component
public class AdminInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${admin.reverso.name}")
    private String adminReversoName;

    @Value("${admin.reverso.lastname}")
    private String adminReversoLastname;

    @Value("${admin.reverso.username}")
    private String adminReversoUsername;

    @Value("${admin.reverso.email}")
    private String adminReversoEmail;

    @Value("${admin.reverso.birthday}")
    private String adminReversoBirthday;

    @Value("${admin.reverso.password}")
    private String adminReversoPassword;

    @Value("${admin.femsenior.name}")
    private String adminFemseniorName;

    @Value("${admin.femsenior.lastname}")
    private String adminFemseniorLastname;

    @Value("${admin.femsenior.username}")
    private String adminFemseniorUsername;

    @Value("${admin.femsenior.email}")
    private String adminFemseniorEmail;

    @Value("${admin.femsenior.birthday}")
    private String adminFemseniorBirthday;

    @Value("${admin.femsenior.password}")
    private String adminFemseniorPassword;

    @PostConstruct
    public void init() {
        Optional<Role> femseniorAdminRole = roleRepository.findByRole(ERole.FEMSENIORADMIN);
        Optional<Role> reversoAdminRole = roleRepository.findByRole(ERole.REVERSOADMIN);

        if (reversoAdminRole.isPresent() && femseniorAdminRole.isPresent()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            try {
                LocalDate birthdayAdminReverso = LocalDate.parse(adminReversoBirthday, formatter);

                if (userRepository.findByEmail(adminReversoEmail).isEmpty()) {
                    User adminReverso = User.builder()
                            .name(adminReversoName)
                            .lastname(adminReversoLastname)
                            .username(adminReversoUsername)
                            .email(adminReversoEmail)
                            .birthday(birthdayAdminReverso)
                            .password(passwordEncoder.encode(adminReversoPassword))
                            .role(reversoAdminRole.get())
                            .build();

                    userRepository.save(adminReverso);
                    System.out.println("Administrador creado: " + adminReversoEmail);
                }

                LocalDate birthdayAdminFemsenior = LocalDate.parse(adminFemseniorBirthday, formatter);

                if (userRepository.findByEmail(adminFemseniorEmail).isEmpty()) {
                    User adminFemsenior = User.builder()
                            .name(adminFemseniorName)
                            .lastname(adminFemseniorLastname)
                            .username(adminFemseniorUsername)
                            .email(adminFemseniorEmail)
                            .birthday(birthdayAdminFemsenior)
                            .password(passwordEncoder.encode(adminFemseniorPassword))
                            .role(femseniorAdminRole.get())
                            .build();

                    userRepository.save(adminFemsenior);
                    System.out.println("Administrador creado: " + adminFemseniorEmail);
                }
            } catch (DateTimeParseException e) {
                System.out.println("Error al analizar la fecha: " + e.getMessage());
            }
        } else {
            System.out.println("Error: El rol ADMIN no existe en la base de datos.");
        }
    }
}
