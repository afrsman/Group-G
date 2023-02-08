package com.codebridgecommunity.jhipsterapp1.service.mapper;

import com.codebridgecommunity.jhipsterapp1.domain.Products;
import com.codebridgecommunity.jhipsterapp1.service.dto.ProductsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Products} and its DTO {@link ProductsDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductsMapper extends EntityMapper<ProductsDTO, Products> {}
