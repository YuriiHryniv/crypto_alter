package com.negeso.crypto.data.service;

import com.negeso.crypto.config.BinanceRestConfig;
import com.negeso.crypto.data.dto.RegistrationDto;
import com.negeso.crypto.data.entity.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.negeso.crypto.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.sasl.AuthenticationException;

@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final BinanceRestConfig binanceRestConfig;

    @Autowired
    public UserService(UserRepository repository,
                       PasswordEncoder passwordEncoder,
                       BinanceRestConfig binanceRestConfig) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.binanceRestConfig = binanceRestConfig;
    }

    public void authenticate(String username, String password) throws AuthenticationException {
        Optional<User> user = repository.findByEmail(username);
        if (user.isEmpty() || !passwordEncoder.matches(password, user.get().getHashedPassword())) {
            throw new AuthenticationException("Invalid username or password: " + username + " " + password);
        }
    }

    public void registerUser(RegistrationDto registrationDto) {
        User user = new User();
        user.setEmail(registrationDto.getEmail());
        user.setHashedPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setProfilePictureUrl(registrationDto.getProfilePictureUrl());
        user.setAutoTrading(false);
        user.setGamblingLimit(BigDecimal.ZERO);
        user.setRole("USER");
        user.setCurrentNumberOfGambles(0);
        repository.save(user);
    }

    public void updateAccountInfo(User user) {
        repository.save(user);
        /*repository.updateAccountInfo(user.getProfilePictureUrl(),
                user.getAutoTrading(),
                user.getGamblingLimit(),
                user.getBinanceApiKey(),
                user.getBinanceApiSecret(),
                user.getEmail(),
                user.getMaxConcurrentGambles(),
                user.getCodes());

         */
    }

    public Optional<User> get(String email) {
        return repository.findByEmail(email);
    }

    @Transactional
    public void updateEmail(String newEmail, String oldEmail) {
        repository.updateEmail(newEmail, oldEmail);
    }

    @Transactional
    public User update(User entity) {
        entity.setRole("USER");
        return repository.save(entity);
    }

    public void delete(String email) {
        repository.deleteByEmail(email);
    }

    public Page<User> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

    public List<User> getAllUsersByAutoBuyingTrue() {
        return repository.findAllByAutoTradingTrue();
    }

    public List<User> getUserByEmail(String email) {
        return repository.findAllByEmailEquals(email);
    }
}
