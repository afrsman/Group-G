package com.codebridgecommunity.jhipsterapp1.repository;

import com.codebridgecommunity.jhipsterapp1.domain.Products;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Products entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductsRepository extends ReactiveCrudRepository<Products, Long>, ProductsRepositoryInternal {
    Flux<Products> findAllBy(Pageable pageable);

    @Override
    <S extends Products> Mono<S> save(S entity);

    @Override
    Flux<Products> findAll();

    @Override
    Mono<Products> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProductsRepositoryInternal {
    <S extends Products> Mono<S> save(S entity);

    Flux<Products> findAllBy(Pageable pageable);

    Flux<Products> findAll();

    Mono<Products> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Products> findAllBy(Pageable pageable, Criteria criteria);

}
