package com.example.pp.mapper;

import com.example.pp.entity.Client;
import com.example.pp.entity.ClientInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface ClientInfoMapper {
    @Mapping(target = "name", expression = "java(getNameClient(client))")
    @Mapping(target = "surname", expression = "java(getSurnameClient(client))")
    @Mapping(target = "phone", source = "phone")
    ClientInfo toClientInfo(Client client);

    default String getNameClient(Client client) {
        String[] parsFullName = client.getFullName().split(" ");
        if (parsFullName.length > 2) {
            return parsFullName[1];
        } else {
            return "nullName";
        }
    }

    default String getSurnameClient(Client client) {
        String[] parsFullName = client.getFullName().split(" ");
        if (parsFullName.length > 1) {
            return parsFullName[0];
        } else {
            return "nullSurname";
        }
    }
}
