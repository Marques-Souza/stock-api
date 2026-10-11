package com.marquesdev.stock.service;

import com.marquesdev.stock.dto.ClientRequestDto;
import com.marquesdev.stock.dto.ClientResponseDto;
import com.marquesdev.stock.dto.ClientUpdatRequestDto;
import com.marquesdev.stock.entity.Client;
import com.marquesdev.stock.exception.CnpjUniqueException;
import com.marquesdev.stock.exception.EmailUniqueException;
import com.marquesdev.stock.exception.EntityNotFoundException;
import com.marquesdev.stock.mapper.ClientMapper;
import com.marquesdev.stock.repository.ClientRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Transactional
    public ClientResponseDto saveClient(ClientRequestDto clientRequestDto){
        if (clientRepository.existsByEmail(clientRequestDto.email())){
            throw new EmailUniqueException("Email Already exists.");
        }

        if (clientRepository.existsByCnpj(clientRequestDto.cnpj())){
            throw new CnpjUniqueException("CNPJ Already exists.");
        }
        Client client = clientMapper.toClient(clientRequestDto);
        Client savedClient = clientRepository.save(client);
        return clientMapper.toDto(savedClient);
    }

    @Transactional(readOnly = true)
    public ClientResponseDto findById(UUID id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));
        return clientMapper.toDto(client);
    }


    @Transactional(readOnly = true)
    public Page<ClientResponseDto> findAll(String search, Pageable pageable) {
        Page<Client> clientsPage = clientRepository.findAll(pageable);

        if (search != null && !search.isBlank()){
            clientsPage = clientRepository.findBySearch(search, pageable);
        }
        return clientsPage.map(clientMapper::toDto);
    }

    @Transactional
    public ClientResponseDto updateClient(UUID id, @Valid ClientUpdatRequestDto clientUpdatRequestDto) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        clientMapper.updateClient(clientUpdatRequestDto, client);
        Client updatedClient = clientRepository.save(client);
        return clientMapper.toDto(updatedClient);

    }
}
