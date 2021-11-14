package com.thoughtworks.auctionservice.repository;

import com.thoughtworks.auctionservice.repository.entity.RequestProposalEntity;
import com.thoughtworks.auctionservice.repository.entity.SignedServiceEntity;
import com.thoughtworks.auctionservice.repository.mapper.RequestProposalMapper;
import com.thoughtworks.auctionservice.repository.mapper.SignedServiceMapper;
import com.thoughtworks.auctionservice.service.bo.model.RequestProposal;
import com.thoughtworks.auctionservice.service.bo.model.SignedService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class AuctionRepositoryTests {
    @Container
    private static final MySQLContainer MY_SQL_CONTAINER = new MySQLContainer("mysql:8.0")
            .withUsername("test")
            .withPassword("123456")
            .withDatabaseName("auction_service");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.password", MY_SQL_CONTAINER::getPassword);
        registry.add("spring.datasource.username", MY_SQL_CONTAINER::getUsername);
    }

    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private RequestProposalMapper requestProposalMapper;
    @Autowired
    private SignedServiceMapper signedServiceMapper;

    @Test
    public void testGetRequestProposalByRequestId() {
        String requestId = "requestId";
        RequestProposal shouldBeNull = auctionRepository.getRequestProposalByRequestId(requestId);
        assertNull(shouldBeNull);

        RequestProposalEntity entity = new RequestProposalEntity();
        entity.setRequestId(requestId);
        entity.setBelongsTo("userId");
        entity.setContent("content");
        entity.setId("id");
        entity.setCreatedAt(Instant.now());
        entity.setExpiredAt(Instant.now());
        requestProposalMapper.saveAndFlush(entity);

        RequestProposal proposal = auctionRepository.getRequestProposalByRequestId(requestId);
        assertEquals(entity.getId(), proposal.getId());
    }

    @Test
    public void testGetSignedServiceByProposalId() {
        String proposalId = "proposalId";
        SignedService shouldBeNull = auctionRepository.getSignedServiceByProposalId(proposalId);
        assertNull(shouldBeNull);

        SignedServiceEntity entity = new SignedServiceEntity();
        entity.setId("id");
        entity.setBelongsTo("userId");
        entity.setProposalId(proposalId);
        entity.setCreatedAt(Instant.now());
        signedServiceMapper.saveAndFlush(entity);

        SignedService signedService = auctionRepository.getSignedServiceByProposalId(proposalId);
        assertEquals(entity.getId(), signedService.getId());
    }

    @Test
    public void testCreateSignedService() {
        SignedService signedService = new SignedService();
        signedService.setId("id");
        signedService.setBelongsTo("userId");
        signedService.setProposalId("proposalId");
        signedService.setCreatedAt(LocalDateTime.now());
        auctionRepository.createSignedService(signedService);

        List<SignedServiceEntity> entities = signedServiceMapper.findAll();
        assertEquals(1, entities.size());
        assertEquals(signedService.getId(), entities.get(0).getId());
    }
}
