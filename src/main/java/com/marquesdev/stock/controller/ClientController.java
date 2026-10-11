package com.marquesdev.stock.controller;

import com.marquesdev.stock.dto.ClientRequestDto;
import com.marquesdev.stock.dto.ClientResponseDto;
import com.marquesdev.stock.dto.ClientUpdatRequestDto;
import com.marquesdev.stock.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('USER')")
    public ResponseEntity<ClientResponseDto> findById(@PathVariable UUID id){
        ClientResponseDto client = clientService.findById(id);
        return ResponseEntity.ok(client);

    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('USER')")
    public ResponseEntity<Page<ClientResponseDto>> findAll(
            @RequestHeader(value = "search", required = false)String search,
            @PageableDefault(page = 0, size = 10, sort = "name") Pageable pageable
            ){
        Page<ClientResponseDto> clients = clientService.findAll(search, pageable);
        return ResponseEntity.ok(clients);
    }



    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ClientResponseDto> updateClient(@PathVariable UUID id, @RequestBody @Valid ClientUpdatRequestDto clientUpdatRequestDto){
        ClientResponseDto client = clientService.updateClient(id, clientUpdatRequestDto);
        return ResponseEntity.ok(client);

    }


}
