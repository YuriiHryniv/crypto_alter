package com.negeso.crypto.data.repository;

import com.negeso.crypto.data.entity.Code;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CodeRepository extends JpaRepository<Code, Long > {

    Optional<Code> getCodeByName(String codeName);




}
