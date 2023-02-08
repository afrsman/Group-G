package com.codebridgecommunity.jhipsterapp1.repository.rowmapper;

import com.codebridgecommunity.jhipsterapp1.domain.Products;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Products}, with proper type conversions.
 */
@Service
public class ProductsRowMapper implements BiFunction<Row, String, Products> {

    private final ColumnConverter converter;

    public ProductsRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Products} stored in the database.
     */
    @Override
    public Products apply(Row row, String prefix) {
        Products entity = new Products();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setPrice(converter.fromRow(row, prefix + "_price", Double.class));
        entity.setSize(converter.fromRow(row, prefix + "_size", String.class));
        return entity;
    }
}
