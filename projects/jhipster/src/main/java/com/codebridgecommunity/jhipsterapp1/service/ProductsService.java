package com.codebridgecommunity.jhipsterapp1.service;

import com.codebridgecommunity.jhipsterapp1.service.dto.ProductsDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.codebridgecommunity.jhipsterapp1.domain.Products}.
 */
public interface ProductsService {
    /**
     * Save a products.
     *
     * @param productsDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ProductsDTO> save(ProductsDTO productsDTO);

    /**
     * Updates a products.
     *
     * @param productsDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ProductsDTO> update(ProductsDTO productsDTO);

    /**
     * Partially updates a products.
     *
     * @param productsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ProductsDTO> partialUpdate(ProductsDTO productsDTO);

    /**
     * Get all the products.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ProductsDTO> findAll(Pageable pageable);

    /**
     * Returns the number of products available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" products.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ProductsDTO> findOne(Long id);

    /**
     * Delete the "id" products.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
