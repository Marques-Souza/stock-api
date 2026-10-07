package com.marquesdev.stock.controller;

import com.marquesdev.stock.dto.ClientRequestDto;
import com.marquesdev.stock.dto.ClientResponseDto;
import com.marquesdev.stock.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${app.api.endpoints.clients}")
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ClientResponseDto> createClient(@RequestBody @Valid ClientRequestDto clientRequestDto){
        ClientResponseDto client = clientService.saveClient(clientRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(client);
    }
}
