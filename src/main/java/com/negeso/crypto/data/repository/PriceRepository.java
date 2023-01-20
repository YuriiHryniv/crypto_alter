package com.negeso.crypto.data.repository;

import com.negeso.crypto.data.entity.Code;
import com.negeso.crypto.data.entity.Price;
import com.negeso.crypto.data.entity.PriceId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PriceRepository extends JpaRepository<Price, PriceId> {

    List<Price> findAllByTimeLessThan(LocalDateTime localDateTime);

    Optional<Price> findFirstByCodeOrderByTimeDesc(Code code);

}
