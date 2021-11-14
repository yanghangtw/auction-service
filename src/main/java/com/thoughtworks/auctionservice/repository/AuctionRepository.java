package com.thoughtworks.auctionservice.repository;

import com.thoughtworks.auctionservice.service.bo.model.RequestProposal;
import com.thoughtworks.auctionservice.service.bo.model.SignedService;

public interface AuctionRepository {
    RequestProposal getRequestProposalByRequestId(String requestId);

    SignedService getSignedServiceByProposalId(String proposalId);

    void createSignedService(SignedService signedService);
}
