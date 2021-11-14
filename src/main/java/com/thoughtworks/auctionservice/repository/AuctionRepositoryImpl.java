package com.thoughtworks.auctionservice.repository;

import com.thoughtworks.auctionservice.repository.entity.RequestProposalEntity;
import com.thoughtworks.auctionservice.repository.entity.SignedServiceEntity;
import com.thoughtworks.auctionservice.repository.mapper.RequestProposalMapper;
import com.thoughtworks.auctionservice.repository.mapper.SignedServiceMapper;
import com.thoughtworks.auctionservice.service.bo.model.RequestProposal;
import com.thoughtworks.auctionservice.service.bo.model.SignedService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Repository
public class AuctionRepositoryImpl implements AuctionRepository {

    @Autowired
    private RequestProposalMapper requestProposalMapper;
    @Autowired
    private SignedServiceMapper signedServiceMapper;

    @Override
    public RequestProposal getRequestProposalByRequestId(String requestId) {
        RequestProposalEntity example = new RequestProposalEntity();
        example.setRequestId(requestId);
        return requestProposalMapper.findOne(Example.of(example)).map(this::ofEntity).orElse(null);
    }

    @Override
    public SignedService getSignedServiceByProposalId(String proposalId) {
        SignedServiceEntity example = new SignedServiceEntity();
        example.setProposalId(proposalId);
        return signedServiceMapper.findOne(Example.of(example)).map(this::ofEntity).orElse(null);
    }

    @Override
    public void createSignedService(SignedService signedService) {
        signedServiceMapper.saveAndFlush(ofModel(signedService));
    }

    private RequestProposal ofEntity(RequestProposalEntity entity) {
        RequestProposal requestProposal = new RequestProposal();
        BeanUtils.copyProperties(entity, requestProposal);
        requestProposal.setCreatedAt(LocalDateTime.ofInstant(entity.getCreatedAt(), ZoneId.systemDefault()));
        requestProposal.setExpiredAt(LocalDateTime.ofInstant(entity.getExpiredAt(), ZoneId.systemDefault()));
        return requestProposal;
    }

    private SignedService ofEntity(SignedServiceEntity entity) {
        SignedService signedService = new SignedService();
        BeanUtils.copyProperties(entity, signedService);
        signedService.setCreatedAt(LocalDateTime.ofInstant(entity.getCreatedAt(), ZoneId.systemDefault()));
        if (null != entity.getEndAt()) {
            signedService.setEndAt(LocalDateTime.ofInstant(entity.getEndAt(), ZoneId.systemDefault()));
        }
        return signedService;
    }

    private SignedServiceEntity ofModel(SignedService signedService) {
        Instant now = Instant.now();
        SignedServiceEntity entity = new SignedServiceEntity();
        BeanUtils.copyProperties(signedService, entity);
        entity.setCreatedAt(signedService.getCreatedAt().toInstant(ZoneId.systemDefault().getRules().getOffset(now)));
        if (null != signedService.getEndAt()) {
            entity.setEndAt(signedService.getEndAt().toInstant(ZoneId.systemDefault().getRules().getOffset(now)));
        }
        return entity;
    }
}
