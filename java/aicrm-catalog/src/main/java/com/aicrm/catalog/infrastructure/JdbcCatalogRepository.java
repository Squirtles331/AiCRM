package com.aicrm.catalog.infrastructure;

import com.aicrm.catalog.domain.CatalogRepository;
import com.aicrm.catalog.domain.PriceItem;
import com.aicrm.catalog.domain.PriceList;
import com.aicrm.catalog.domain.Product;
import com.aicrm.catalog.domain.ProductCategory;
import com.aicrm.kernel.page.PageResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** PostgreSQL adapter for tenant-scoped catalog records. */
@Repository
public class JdbcCatalogRepository implements CatalogRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcCatalogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ProductCategory insertCategory(ProductCategory category, long actorId) {
        jdbcTemplate.update("insert into crm_product_category (id,tenant_id,parent_id,code,name,status,sort_order,version,created_by,updated_by,created_at,updated_at) "
                        + "values (?,?,?,?,?,?,?,?,?,?,?,?)", category.id(), category.tenantId(), category.parentId(), category.code(),
                category.name(), category.status().name(), category.sortOrder(), category.version(), actorId, actorId,
                timestamp(category.createdAt()), timestamp(category.updatedAt()));
        return findCategory(category.tenantId(), category.id()).orElseThrow();
    }

    @Override
    public Product insertProduct(Product product, long actorId) {
        jdbcTemplate.update("insert into crm_product (id,tenant_id,category_id,product_no,sku,name,specification,unit,status,sale_enabled,version,created_by,updated_by,created_at,updated_at) "
                        + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", product.id(), product.tenantId(), product.categoryId(),
                product.productNo(), product.sku(), product.name(), product.specification(), product.unit(), product.status().name(),
                product.saleEnabled(), product.version(), actorId, actorId, timestamp(product.createdAt()), timestamp(product.updatedAt()));
        return findProduct(product.tenantId(), product.id()).orElseThrow();
    }

    @Override
    public PriceList insertPriceList(PriceList priceList, long actorId) {
        jdbcTemplate.update("insert into crm_price_list (id,tenant_id,code,name,currency,status,effective_from,effective_to,version,created_by,updated_by,created_at,updated_at) "
                        + "values (?,?,?,?,?,?,?,?,?,?,?,?,?)", priceList.id(), priceList.tenantId(), priceList.code(), priceList.name(),
                priceList.currency(), priceList.status().name(), timestamp(priceList.effectiveFrom()), timestamp(priceList.effectiveTo()),
                priceList.version(), actorId, actorId, timestamp(priceList.createdAt()), timestamp(priceList.updatedAt()));
        return findPriceList(priceList.tenantId(), priceList.id()).orElseThrow();
    }

    @Override
    public PriceItem insertPriceItem(PriceItem item, long actorId) {
        jdbcTemplate.update("insert into crm_price_item (id,tenant_id,price_list_id,product_id,list_price,minimum_price,tax_rate,status,version,created_by,updated_by,created_at,updated_at) "
                        + "values (?,?,?,?,?,?,?,?,?,?,?,?,?)", item.id(), item.tenantId(), item.priceListId(), item.productId(),
                item.listPrice(), item.minimumPrice(), item.taxRate(), item.status().name(), item.version(), actorId, actorId,
                timestamp(item.createdAt()), timestamp(item.updatedAt()));
        return findPriceItem(item.tenantId(), item.id()).orElseThrow();
    }

    @Override
    public Optional<ProductCategory> findCategory(long tenantId, long categoryId) {
        return one("select * from crm_product_category where tenant_id=? and id=? and deleted_at is null", categoryMapper(), tenantId, categoryId);
    }

    @Override
    public Optional<Product> findProduct(long tenantId, long productId) {
        return one("select * from crm_product where tenant_id=? and id=? and deleted_at is null", productMapper(), tenantId, productId);
    }

    @Override
    public Optional<PriceList> findPriceList(long tenantId, long priceListId) {
        return one("select * from crm_price_list where tenant_id=? and id=? and deleted_at is null", priceListMapper(), tenantId, priceListId);
    }

    @Override
    public Optional<PriceItem> findPriceItem(long tenantId, long priceItemId) {
        return one("select * from crm_price_item where tenant_id=? and id=? and deleted_at is null", priceItemMapper(), tenantId, priceItemId);
    }

    @Override
    public PageResult<Product> findProducts(long tenantId, long page, long size) {
        Long total = jdbcTemplate.queryForObject("select count(1) from crm_product where tenant_id=? and deleted_at is null", Long.class, tenantId);
        List<Product> records = jdbcTemplate.query("select * from crm_product where tenant_id=? and deleted_at is null "
                        + "order by updated_at desc,id desc limit ? offset ?", productMapper(), tenantId, size, (page - 1) * size);
        return new PageResult<>(records, page, size, total == null ? 0 : total);
    }

    @Override
    public List<ProductCategory> findCategories(long tenantId) {
        return jdbcTemplate.query("select * from crm_product_category where tenant_id=? and deleted_at is null order by sort_order,id", categoryMapper(), tenantId);
    }

    @Override
    public List<PriceItem> findPriceItems(long tenantId, long priceListId) {
        return jdbcTemplate.query("select * from crm_price_item where tenant_id=? and price_list_id=? and deleted_at is null order by id", priceItemMapper(), tenantId, priceListId);
    }

    @Override
    public boolean hasActivePriceItems(long tenantId, long priceListId) {
        Boolean result = jdbcTemplate.query("select exists(select 1 from crm_price_item where tenant_id=? and price_list_id=? "
                        + "and status='ACTIVE' and deleted_at is null)", rs -> rs.next() && rs.getBoolean(1), tenantId, priceListId);
        return Boolean.TRUE.equals(result);
    }

    @Override
    public boolean publishPriceList(long tenantId, long priceListId, long expectedVersion, long actorId) {
        return jdbcTemplate.update("update crm_price_list set status='ACTIVE', effective_from=coalesce(effective_from,now()), "
                        + "version=version+1,updated_by=?,updated_at=now() where tenant_id=? and id=? and status='DRAFT' "
                        + "and version=? and deleted_at is null", actorId, tenantId, priceListId, expectedVersion) == 1;
    }

    private <T> Optional<T> one(String sql, RowMapper<T> mapper, Object... args) {
        return jdbcTemplate.query(sql, mapper, args).stream().findFirst();
    }
    private RowMapper<ProductCategory> categoryMapper() {
        return (rs, row) -> new ProductCategory(rs.getLong("id"), rs.getLong("tenant_id"), nullableLong(rs, "parent_id"),
                rs.getString("code"), rs.getString("name"), ProductCategory.Status.valueOf(rs.getString("status")),
                rs.getInt("sort_order"), rs.getLong("version"), instant(rs, "created_at"), instant(rs, "updated_at"));
    }
    private RowMapper<Product> productMapper() {
        return (rs, row) -> new Product(rs.getLong("id"), rs.getLong("tenant_id"), nullableLong(rs, "category_id"),
                rs.getString("product_no"), rs.getString("sku"), rs.getString("name"), rs.getString("specification"),
                rs.getString("unit"), Product.Status.valueOf(rs.getString("status")), rs.getBoolean("sale_enabled"),
                rs.getLong("version"), instant(rs, "created_at"), instant(rs, "updated_at"));
    }
    private RowMapper<PriceList> priceListMapper() {
        return (rs, row) -> new PriceList(rs.getLong("id"), rs.getLong("tenant_id"), rs.getString("code"), rs.getString("name"),
                rs.getString("currency"), PriceList.Status.valueOf(rs.getString("status")), instant(rs, "effective_from"),
                instant(rs, "effective_to"), rs.getLong("version"), instant(rs, "created_at"), instant(rs, "updated_at"));
    }
    private RowMapper<PriceItem> priceItemMapper() {
        return (rs, row) -> new PriceItem(rs.getLong("id"), rs.getLong("tenant_id"), rs.getLong("price_list_id"),
                rs.getLong("product_id"), rs.getBigDecimal("list_price"), rs.getBigDecimal("minimum_price"),
                rs.getBigDecimal("tax_rate"), PriceItem.Status.valueOf(rs.getString("status")), rs.getLong("version"),
                instant(rs, "created_at"), instant(rs, "updated_at"));
    }
    private Long nullableLong(ResultSet rs, String column) throws SQLException { long value = rs.getLong(column); return rs.wasNull() ? null : value; }
    private Instant instant(ResultSet rs, String column) throws SQLException { Timestamp value = rs.getTimestamp(column); return value == null ? null : value.toInstant(); }
    private Timestamp timestamp(Instant value) { return value == null ? null : Timestamp.from(value); }
}
