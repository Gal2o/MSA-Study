package com.example.catalogservice.service;

import com.example.catalogservice.jpa.CatalogEntity;
import com.example.catalogservice.vo.ResponseCatalog;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CatalogMapper {
    CatalogMapper instance = Mappers.getMapper(CatalogMapper.class);

    ResponseCatalog entityToResponseCatalog(CatalogEntity catalogEntity);
}
