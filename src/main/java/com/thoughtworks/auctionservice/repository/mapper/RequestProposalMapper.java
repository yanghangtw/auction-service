package com.thoughtworks.auctionservice.repository.mapper;

import com.thoughtworks.auctionservice.repository.entity.RequestProposalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface RequestProposalMapper extends JpaRepository<RequestProposalEntity, String> {
}
