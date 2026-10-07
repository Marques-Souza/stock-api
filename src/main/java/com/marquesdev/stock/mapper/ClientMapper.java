package com.marquesdev.stock.mapper;

import com.marquesdev.stock.ResourceUrlBuilder.ResourceUrlBuilder;
import com.marquesdev.stock.dto.ClientRequestDto;
import com.marquesdev.stock.dto.ClientResponseDto;
import com.marquesdev.stock.entity.Client;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;

@Component
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientMapper {

    private final ModelMapper modelMapper;
    private final ResourceUrlBuilder resourceUrlBuilder;


    @Value("${app.api.endpoints.clients}")
    private String clientEndpoint;

    public Client toClient(ClientRequestDto dto){
        Client client = new Client();

        client.setName(dto.name());
        client.setCnpj(dto.cnpj());
        client.setEmail(dto.email());
        client.setPhone(dto.phone());

        return client;
    }

    public ClientResponseDto toDto(Client client){
        return new ClientResponseDto(
                client.getId(),
                client.getName(),
                client.getCnpj(),
                client.getEmail(),
                client.getPhone(),
                resourceUrlBuilder.build(clientEndpoint, client.getId())
        );
    }
}
