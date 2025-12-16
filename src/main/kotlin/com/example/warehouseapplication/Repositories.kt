package com.example.warehouseapplication

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface WarehouseRepository : JpaRepository<Warehouse, Long> {
    fun findAllByActiveTrue(): List<Warehouse>
}

interface WorkerRepository : JpaRepository<Worker, Long> {
    fun findAllByActiveTrue(): List<Worker>
    fun findByEmployeeCode(employeeCode: String): Worker?
}

interface CategoryRepository : JpaRepository<Category, Long> {
    fun findAllByActiveTrue(): List<Category>
}

interface UnitOfMeasureRepository : JpaRepository<UnitOfMeasure, Long> {
    fun findAllByActiveTrue(): List<UnitOfMeasure>
}

interface CurrencyRepository : JpaRepository<Currency, Long> {
    fun findAllByActiveTrue(): List<Currency>
}

interface SupplierRepository : JpaRepository<Supplier, Long> {
    fun findAllByActiveTrue(): List<Supplier>
}

interface ProductRepository : JpaRepository<Product, Long> {
    fun findAllByActiveTrue(): List<Product>
    fun findByProductCode(productCode: String): Product?
}

/** NEW: file metadatalari (files jadvali) */
interface FileAssetRepository : JpaRepository<FileAsset, Long> {
    fun findByStorageKey(storageKey: String): FileAsset?
}

/** FIXED: product_images jadvali uchun repository */
interface ProductImageRepository : JpaRepository<ProductImage, Long> {
    fun findAllByProductId(productId: Long): List<ProductImage>
}

interface StockEntryRepository : JpaRepository<StockEntry, Long> {
    fun findByDate(date: LocalDate): List<StockEntry>
    fun findByWarehouseId(warehouseId: Long): List<StockEntry>
}

interface StockEntryItemRepository : JpaRepository<StockEntryItem, Long> {

    @Query(
        """
        select i 
        from StockEntryItem i
        where i.stockEntry.date = :date
          and i.active = true
        """
    )
    fun findActiveItemsByEntryDate(@Param("date") date: LocalDate): List<StockEntryItem>

    @Query(
        """
        select i
        from StockEntryItem i
        where i.expiryDate is not null
          and i.expiryDate between :from and :to
          and i.active = true
        """
    )
    fun findItemsExpiringBetween(
        @Param("from") from: LocalDate,
        @Param("to") to: LocalDate
    ): List<StockEntryItem>
}

interface SaleRepository : JpaRepository<Sale, Long> {
    fun findByDate(date: LocalDate): List<Sale>
    fun findByWarehouseId(warehouseId: Long): List<Sale>
}

interface SaleItemRepository : JpaRepository<SaleItem, Long> {

    @Query(
        """
        select i
        from SaleItem i
        where i.sale.date = :date
          and i.active = true
        """
    )
    fun findActiveItemsBySaleDate(@Param("date") date: LocalDate): List<SaleItem>
}

interface ProductStockRepository : JpaRepository<ProductStock, Long> {

    fun findByWarehouseIdAndProductId(
        warehouseId: Long,
        productId: Long
    ): ProductStock?

    fun findAllByWarehouseId(warehouseId: Long): List<ProductStock>

    fun findAllByProductId(productId: Long): List<ProductStock>
}

interface NotificationSettingRepository : JpaRepository<NotificationSetting, Long> {
    fun findByKey(key: String): NotificationSetting?
}

interface ExpiryNotificationRepository : JpaRepository<ExpiryNotification, Long> {
    fun existsByStockEntryItemIdAndChatId(stockEntryItemId: Long, chatId: String): Boolean
}
