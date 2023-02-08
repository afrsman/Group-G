package com.codebridgecommunity.jhipsterapp1.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.codebridgecommunity.jhipsterapp1.IntegrationTest;
import com.codebridgecommunity.jhipsterapp1.domain.Products;
import com.codebridgecommunity.jhipsterapp1.repository.EntityManager;
import com.codebridgecommunity.jhipsterapp1.repository.ProductsRepository;
import com.codebridgecommunity.jhipsterapp1.service.dto.ProductsDTO;
import com.codebridgecommunity.jhipsterapp1.service.mapper.ProductsMapper;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link ProductsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProductsResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Double DEFAULT_PRICE = 1D;
    private static final Double UPDATED_PRICE = 2D;

    private static final String DEFAULT_SIZE = "AAAAAAAAAA";
    private static final String UPDATED_SIZE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private ProductsMapper productsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Products products;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Products createEntity(EntityManager em) {
        Products products = new Products().name(DEFAULT_NAME).price(DEFAULT_PRICE).size(DEFAULT_SIZE);
        return products;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Products createUpdatedEntity(EntityManager em) {
        Products products = new Products().name(UPDATED_NAME).price(UPDATED_PRICE).size(UPDATED_SIZE);
        return products;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Products.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        products = createEntity(em);
    }

    @Test
    void createProducts() throws Exception {
        int databaseSizeBeforeCreate = productsRepository.findAll().collectList().block().size();
        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productsDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll().collectList().block();
        assertThat(productsList).hasSize(databaseSizeBeforeCreate + 1);
        Products testProducts = productsList.get(productsList.size() - 1);
        assertThat(testProducts.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProducts.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testProducts.getSize()).isEqualTo(DEFAULT_SIZE);
    }

    @Test
    void createProductsWithExistingId() throws Exception {
        // Create the Products with an existing ID
        products.setId(1L);
        ProductsDTO productsDTO = productsMapper.toDto(products);

        int databaseSizeBeforeCreate = productsRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll().collectList().block();
        assertThat(productsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = productsRepository.findAll().collectList().block().size();
        // set the field null
        products.setName(null);

        // Create the Products, which fails.
        ProductsDTO productsDTO = productsMapper.toDto(products);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Products> productsList = productsRepository.findAll().collectList().block();
        assertThat(productsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = productsRepository.findAll().collectList().block().size();
        // set the field null
        products.setPrice(null);

        // Create the Products, which fails.
        ProductsDTO productsDTO = productsMapper.toDto(products);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Products> productsList = productsRepository.findAll().collectList().block();
        assertThat(productsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllProducts() {
        // Initialize the database
        productsRepository.save(products).block();

        // Get all the productsList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(products.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].price")
            .value(hasItem(DEFAULT_PRICE.doubleValue()))
            .jsonPath("$.[*].size")
            .value(hasItem(DEFAULT_SIZE));
    }

    @Test
    void getProducts() {
        // Initialize the database
        productsRepository.save(products).block();

        // Get the products
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, products.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(products.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.price")
            .value(is(DEFAULT_PRICE.doubleValue()))
            .jsonPath("$.size")
            .value(is(DEFAULT_SIZE));
    }

    @Test
    void getNonExistingProducts() {
        // Get the products
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProducts() throws Exception {
        // Initialize the database
        productsRepository.save(products).block();

        int databaseSizeBeforeUpdate = productsRepository.findAll().collectList().block().size();

        // Update the products
        Products updatedProducts = productsRepository.findById(products.getId()).block();
        updatedProducts.name(UPDATED_NAME).price(UPDATED_PRICE).size(UPDATED_SIZE);
        ProductsDTO productsDTO = productsMapper.toDto(updatedProducts);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, productsDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productsDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll().collectList().block();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
        Products testProducts = productsList.get(productsList.size() - 1);
        assertThat(testProducts.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProducts.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testProducts.getSize()).isEqualTo(UPDATED_SIZE);
    }

    @Test
    void putNonExistingProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().collectList().block().size();
        products.setId(count.incrementAndGet());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, productsDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll().collectList().block();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().collectList().block().size();
        products.setId(count.incrementAndGet());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll().collectList().block();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().collectList().block().size();
        products.setId(count.incrementAndGet());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productsDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll().collectList().block();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateProductsWithPatch() throws Exception {
        // Initialize the database
        productsRepository.save(products).block();

        int databaseSizeBeforeUpdate = productsRepository.findAll().collectList().block().size();

        // Update the products using partial update
        Products partialUpdatedProducts = new Products();
        partialUpdatedProducts.setId(products.getId());

        partialUpdatedProducts.name(UPDATED_NAME).size(UPDATED_SIZE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProducts.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProducts))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll().collectList().block();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
        Products testProducts = productsList.get(productsList.size() - 1);
        assertThat(testProducts.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProducts.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testProducts.getSize()).isEqualTo(UPDATED_SIZE);
    }

    @Test
    void fullUpdateProductsWithPatch() throws Exception {
        // Initialize the database
        productsRepository.save(products).block();

        int databaseSizeBeforeUpdate = productsRepository.findAll().collectList().block().size();

        // Update the products using partial update
        Products partialUpdatedProducts = new Products();
        partialUpdatedProducts.setId(products.getId());

        partialUpdatedProducts.name(UPDATED_NAME).price(UPDATED_PRICE).size(UPDATED_SIZE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProducts.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProducts))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll().collectList().block();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
        Products testProducts = productsList.get(productsList.size() - 1);
        assertThat(testProducts.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProducts.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testProducts.getSize()).isEqualTo(UPDATED_SIZE);
    }

    @Test
    void patchNonExistingProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().collectList().block().size();
        products.setId(count.incrementAndGet());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, productsDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll().collectList().block();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().collectList().block().size();
        products.setId(count.incrementAndGet());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productsDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll().collectList().block();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamProducts() throws Exception {
        int databaseSizeBeforeUpdate = productsRepository.findAll().collectList().block().size();
        products.setId(count.incrementAndGet());

        // Create the Products
        ProductsDTO productsDTO = productsMapper.toDto(products);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productsDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Products in the database
        List<Products> productsList = productsRepository.findAll().collectList().block();
        assertThat(productsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteProducts() {
        // Initialize the database
        productsRepository.save(products).block();

        int databaseSizeBeforeDelete = productsRepository.findAll().collectList().block().size();

        // Delete the products
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, products.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Products> productsList = productsRepository.findAll().collectList().block();
        assertThat(productsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
