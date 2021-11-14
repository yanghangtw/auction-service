package com.thoughtworks.auctionservice.service;

import com.thoughtworks.auctionservice.repository.AuctionRepository;
import com.thoughtworks.auctionservice.service.bo.exception.*;
import com.thoughtworks.auctionservice.service.bo.model.RequestProposal;
import com.thoughtworks.auctionservice.service.bo.model.SignedService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final IdGenerator idGenerator;

    public AuctionService(AuctionRepository auctionRepository, IdGenerator idGenerator) {
        this.auctionRepository = auctionRepository;
        this.idGenerator = idGenerator;
    }

    public String createAuctionService(String requestId, String userId) {
        RequestProposal requestProposal;
        try {
            requestProposal = auctionRepository.getRequestProposalByRequestId(requestId);
        } catch (Exception e) {
            throw new UnavailableException();
        }

        if (null == requestProposal) {
            throw new NotFoundException();
        }
        if (!requestProposal.getBelongsTo().equals(userId)) {
            throw new AccessDeniedException();
        }
        if (LocalDateTime.now().isAfter(requestProposal.getExpiredAt())) {
            throw new ExpiredException();
        }

        SignedService existedService;
        try {
            existedService = auctionRepository.getSignedServiceByProposalId(requestProposal.getId());
        } catch (Exception e) {
            throw new UnavailableException();
        }
        if (null != existedService) {
            throw new AlreadyExistedException(existedService.getId());
        }

        SignedService signedService = new SignedService();
        signedService.setCreatedAt(LocalDateTime.now());
        signedService.setId(idGenerator.next());
        signedService.setBelongsTo(userId);
        signedService.setProposalId(requestProposal.getId());
        try {
            auctionRepository.createSignedService(signedService);
        } catch (Exception e) {
            throw new UnavailableException();
        }

        return signedService.getId();
    }

}
