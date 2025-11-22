package com.example.tpcm_spring.repository.app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.tpcm_spring.models.app.ThirdParty;

import java.util.List;

@Repository
public interface ThirdPartyRepository extends JpaRepository<ThirdParty, Long> {

    List<ThirdParty> findByNameIgnoreCase(String name);

    List<ThirdParty> findByActive(String active);

    List<ThirdParty> findByServiceTypeIgnoreCase(String serviceType);
}
