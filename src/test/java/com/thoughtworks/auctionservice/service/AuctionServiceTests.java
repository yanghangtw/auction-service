package com.thoughtworks.auctionservice.service;

import com.thoughtworks.auctionservice.repository.AuctionRepository;
import com.thoughtworks.auctionservice.service.bo.exception.*;
import com.thoughtworks.auctionservice.service.bo.model.RequestProposal;
import com.thoughtworks.auctionservice.service.bo.model.SignedService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class AuctionServiceTests {

    private final AuctionRepository auctionRepository = mock(AuctionRepository.class);
    private final IdGenerator idGenerator = mock(IdGenerator.class);
    private final AuctionService auctionService = new AuctionService(auctionRepository, idGenerator);

    @Test
    public void testSuccessfulCreateAuctionService() {
        String requestId = "requestId";
        String userId = "userId";
        RequestProposal requestProposal = new RequestProposal();
        requestProposal.setId("proposalId");
        requestProposal.setRequestId(requestId);
        requestProposal.setBelongsTo(userId);
        requestProposal.setExpiredAt(LocalDateTime.now().plus(1, HOURS));
        when(auctionRepository.getRequestProposalByRequestId(requestId)).thenReturn(requestProposal);
        when(auctionRepository.getSignedServiceByProposalId(requestProposal.getId())).thenReturn(null);
        String nextId = "nextId";
        when(idGenerator.next()).thenReturn(nextId);

        String serviceId = auctionService.createAuctionService(requestId, userId);

        assertEquals(nextId, serviceId);
        ArgumentCaptor<SignedService> signedServiceArgumentCaptor = ArgumentCaptor.forClass(SignedService.class);
        verify(auctionRepository, times(1)).createSignedService(signedServiceArgumentCaptor.capture());
        assertEquals(requestProposal.getId(), signedServiceArgumentCaptor.getValue().getProposalId());
        assertEquals(userId, signedServiceArgumentCaptor.getValue().getBelongsTo());
        assertEquals(nextId, signedServiceArgumentCaptor.getValue().getId());
    }

    @Test
    public void testNoProposalFound() {
        String requestId = "requestId";
        String userId = "userId";
        when(auctionRepository.getRequestProposalByRequestId(requestId)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> {
                    auctionService.createAuctionService(requestId, userId);
                }
        );
    }

    @Test
    public void testNotBelongsToProposal() {
        String requestId = "requestId";
        String userId = "userId";
        RequestProposal requestProposal = new RequestProposal();
        requestProposal.setId("proposalId");
        requestProposal.setRequestId(requestId);
        requestProposal.setBelongsTo("otherUser");
        when(auctionRepository.getRequestProposalByRequestId(requestId)).thenReturn(requestProposal);

        assertThrows(AccessDeniedException.class, () -> {
                    auctionService.createAuctionService(requestId, userId);
                }
        );
    }

    @Test
    public void testProposalExpired() {
        String requestId = "requestId";
        String userId = "userId";
        RequestProposal requestProposal = new RequestProposal();
        requestProposal.setId("proposalId");
        requestProposal.setRequestId(requestId);
        requestProposal.setBelongsTo(userId);
        requestProposal.setExpiredAt(LocalDateTime.now().minus(1, HOURS));
        when(auctionRepository.getRequestProposalByRequestId(requestId)).thenReturn(requestProposal);

        assertThrows(ExpiredException.class, () -> {
                    auctionService.createAuctionService(requestId, userId);
                }
        );
    }

    @Test
    public void testServiceAlreadyCreated() {
        String requestId = "requestId";
        String userId = "userId";
        String serviceId = "serviceId";
        RequestProposal requestProposal = new RequestProposal();
        requestProposal.setId("proposalId");
        requestProposal.setRequestId(requestId);
        requestProposal.setBelongsTo(userId);
        requestProposal.setExpiredAt(LocalDateTime.now().plus(1, HOURS));
        when(auctionRepository.getRequestProposalByRequestId(requestId)).thenReturn(requestProposal);
        SignedService signedService = new SignedService();
        signedService.setId(serviceId);
        when(auctionRepository.getSignedServiceByProposalId(requestProposal.getId())).thenReturn(signedService);

        Exception e = assertThrows(AlreadyExistedException.class, () -> {
                    auctionService.createAuctionService(requestId, userId);
                }
        );
        assertEquals(serviceId, e.getMessage());
    }

    @Test
    public void testGetRequestProposalFailed() {
        String requestId = "requestId";
        String userId = "userId";
        when(auctionRepository.getRequestProposalByRequestId(requestId)).thenThrow(new RuntimeException());

        assertThrows(UnavailableException.class, () -> {
                    auctionService.createAuctionService(requestId, userId);
                }
        );
    }

    @Test
    public void testGetSignedServiceFailed() {
        String requestId = "requestId";
        String userId = "userId";
        RequestProposal requestProposal = new RequestProposal();
        requestProposal.setId("proposalId");
        requestProposal.setRequestId(requestId);
        requestProposal.setBelongsTo(userId);
        requestProposal.setExpiredAt(LocalDateTime.now().plus(1, HOURS));
        when(auctionRepository.getRequestProposalByRequestId(requestId)).thenReturn(requestProposal);
        when(auctionRepository.getSignedServiceByProposalId(requestProposal.getId())).thenThrow(new RuntimeException());

        assertThrows(UnavailableException.class, () -> {
                    auctionService.createAuctionService(requestId, userId);
                }
        );
    }

    @Test
    public void testCreateSignedServiceFailed() {
        String requestId = "requestId";
        String userId = "userId";
        RequestProposal requestProposal = new RequestProposal();
        requestProposal.setId("proposalId");
        requestProposal.setRequestId(requestId);
        requestProposal.setBelongsTo(userId);
        requestProposal.setExpiredAt(LocalDateTime.now().plus(1, HOURS));
        when(auctionRepository.getRequestProposalByRequestId(requestId)).thenReturn(requestProposal);
        when(auctionRepository.getSignedServiceByProposalId(requestProposal.getId())).thenReturn(null);
        doThrow(new RuntimeException()).when(auctionRepository).createSignedService(any(SignedService.class));

        assertThrows(UnavailableException.class, () -> {
                    auctionService.createAuctionService(requestId, userId);
                }
        );
    }
}
