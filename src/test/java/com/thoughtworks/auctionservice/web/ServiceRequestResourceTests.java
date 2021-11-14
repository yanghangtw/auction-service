package com.thoughtworks.auctionservice.web;

import com.thoughtworks.auctionservice.service.AuctionService;
import com.thoughtworks.auctionservice.service.bo.exception.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ServiceRequestResource.class)
public class ServiceRequestResourceTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuctionService auctionService;

    @Test
    public void signContractSuccessfully_returnCreatedURI() throws Exception {
        String requestId = "requestId";
        String userId = "userId";
        String userRole = "Delegate";
        String serviceId = "serviceId";
        when(auctionService.createAuctionService(requestId, userId)).thenReturn(serviceId);

        this.mockMvc.perform(
                        post("/auction-service-requests/{rid}/proposal/contract", requestId)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .header("User-Id", userId)
                                .header("User-Role", userRole)
                )
                .andExpect(status().is(201))
                .andExpect(header().string("Location", mergeURI("/auction-service", serviceId)));
    }

    @Test
    public void proposalNotFound_returnNotFoundError() throws Exception {
        String requestId = "requestId";
        String userId = "userId";
        String userRole = "Delegate";
        when(auctionService.createAuctionService(requestId, userId)).thenThrow(new NotFoundException());

        this.mockMvc.perform(
                        post("/auction-service-requests/{rid}/proposal/contract", requestId)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .header("User-Id", userId)
                                .header("User-Role", userRole)
                )
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.error", is("委托方案不存在")));
    }

    @Test
    public void userNotDelegate_returnForbiddenError() throws Exception {
        String requestId = "requestId";
        String userId = "userId";
        String userRole = "notDelegate";

        this.mockMvc.perform(
                        post("/auction-service-requests/{rid}/proposal/contract", requestId)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .header("User-Id", userId)
                                .header("User-Role", userRole)
                )
                .andExpect(status().is(403))
                .andExpect(jsonPath("$.error", is("用户无权限操作")));
    }

    @Test
    public void userNotBelongsToContract_returnForbiddenError() throws Exception {
        String requestId = "requestId";
        String userId = "userId";
        String userRole = "Delegate";
        when(auctionService.createAuctionService(requestId, userId)).thenThrow(new AccessDeniedException());

        this.mockMvc.perform(
                        post("/auction-service-requests/{rid}/proposal/contract", requestId)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .header("User-Id", userId)
                                .header("User-Role", userRole)
                )
                .andExpect(status().is(403))
                .andExpect(jsonPath("$.error", is("用户无权限操作")));
    }

    @Test
    public void contractAlreadySigned_returnServiceLocation() throws Exception {
        String requestId = "requestId";
        String userId = "userId";
        String userRole = "Delegate";
        String serviceId = "serviceId";
        when(auctionService.createAuctionService(requestId, userId)).thenThrow(new AlreadyExistedException(serviceId));

        this.mockMvc.perform(
                        post("/auction-service-requests/{rid}/proposal/contract", requestId)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .header("User-Id", userId)
                                .header("User-Role", userRole)
                )
                .andExpect(status().is(301))
                .andExpect(header().string("Location", mergeURI("/auction-service", serviceId)));
    }

    @Test
    public void proposalExpired_returnGoneError() throws Exception {
        String requestId = "requestId";
        String userId = "userId";
        String userRole = "Delegate";
        when(auctionService.createAuctionService(requestId, userId)).thenThrow(new ExpiredException());

        this.mockMvc.perform(
                        post("/auction-service-requests/{rid}/proposal/contract", requestId)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .header("User-Id", userId)
                                .header("User-Role", userRole)
                )
                .andExpect(status().is(410))
                .andExpect(jsonPath("$.error", is("委托方案已过期")));
    }

    @Test
    public void serviceUnavailable_returnUnavailableError() throws Exception {
        String requestId = "requestId";
        String userId = "userId";
        String userRole = "Delegate";
        when(auctionService.createAuctionService(requestId, userId)).thenThrow(new UnavailableException());

        this.mockMvc.perform(
                        post("/auction-service-requests/{rid}/proposal/contract", requestId)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .header("User-Id", userId)
                                .header("User-Role", userRole)
                )
                .andExpect(status().is(503))
                .andExpect(jsonPath("$.error", is("服务暂不可用")));
    }

    private String mergeURI(String... parts) {
        return String.join("/", parts);
    }
}
