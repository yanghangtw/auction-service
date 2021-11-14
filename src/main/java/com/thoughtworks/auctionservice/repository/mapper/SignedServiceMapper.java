package com.thoughtworks.auctionservice.repository.mapper;

import com.thoughtworks.auctionservice.repository.entity.SignedServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface SignedServiceMapper extends JpaRepository<SignedServiceEntity, String> {
}
