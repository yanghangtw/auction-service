package com.thoughtworks.auctionservice.web;

import com.thoughtworks.auctionservice.service.AuctionService;
import com.thoughtworks.auctionservice.service.bo.exception.*;
import com.thoughtworks.auctionservice.web.dto.GeneralResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping(path = "/auction-service-requests", produces = MediaType.APPLICATION_JSON_VALUE)
public class ServiceRequestResource {

    @Autowired
    private AuctionService auctionService;

    @PostMapping(path = "/{rid}/proposal/contract")
    public ResponseEntity<GeneralResponse<Void>> signContract(
            @PathVariable("rid") String requestId,
            @RequestHeader("User-Id") String userId,
            @RequestHeader("User-Role") String userRole) {

        if (!userRole.equals("Delegate")) {
            GeneralResponse<Void> response = new GeneralResponse<>();
            response.setError("用户无权限操作");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try {
            String serviceId = auctionService.createAuctionService(requestId, userId);
            return ResponseEntity
                    .created(URI.create(mergeURI("/auction-service", serviceId)))
                    .build();
        } catch (NotFoundException e) {
            GeneralResponse<Void> response = new GeneralResponse<>();
            response.setError("委托方案不存在");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (AccessDeniedException e) {
            GeneralResponse<Void> response = new GeneralResponse<>();
            response.setError("用户无权限操作");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (AlreadyExistedException e) {
            return ResponseEntity
                    .status(HttpStatus.MOVED_PERMANENTLY)
                    .location(URI.create(mergeURI("/auction-service", e.getMessage())))
                    .build();
        } catch (ExpiredException e) {
            GeneralResponse<Void> response = new GeneralResponse<>();
            response.setError("委托方案已过期");
            return ResponseEntity.status(HttpStatus.GONE).body(response);
        } catch (UnavailableException e) {
            GeneralResponse<Void> response = new GeneralResponse<>();
            response.setError("服务暂不可用");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }

    private String mergeURI(String... parts) {
        return String.join("/", parts);
    }
}
