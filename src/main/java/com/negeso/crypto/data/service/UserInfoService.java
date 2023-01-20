package com.negeso.crypto.data.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.negeso.crypto.data.entity.UserInfo;
import com.negeso.crypto.data.repository.UserInfoRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class UserInfoService {
    private final UserInfoRepository repository;

    @Autowired
    public UserInfoService(UserInfoRepository repository) {
        this.repository = repository;
    }

    public Optional<UserInfo> get(String email) {
        return repository.findById(email);
    }

    public UserInfo update(UserInfo entity) {
        return repository.save(entity);
    }

    public void delete(String email) {
        repository.deleteByEmail(email);
    }

    public Page<UserInfo> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

    public String findFirstAndLastName(String email) {
        String firstName = repository.findFirstNameByEmail(email);
        String lastName = repository.findLastNameByEmail(email);
        if (firstName == null && lastName == null) {
            return "Anonymous";
        } else {
            String name = "";
            if (firstName != null) {
                name = name.concat(firstName).concat(" ");
            }
            if (lastName != null) {
                name = name.concat(lastName);
            }
            return name;
        }
    }

    public List<UserInfo> findUserInfoByEmail(String email) {
        return repository.findAllById(new ArrayList<>() {{add(email);}});
    }

    public void deletePersonalInfo(UserInfo personalInfo) {
        repository.delete(personalInfo);
    }

    public void savePersonalInfo(UserInfo personalInfo) {
        if (personalInfo == null) {
            log.error("Personal info is null");
            return;
        }
        repository.save(personalInfo);
    }
}
