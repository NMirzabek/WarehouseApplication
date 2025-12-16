package com.example.warehouseapplication

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val errors: List<String>? = null
)

data class WarehouseCreateRequest(
    val name: String
)

data class WarehouseUpdateRequest(
    val name: String,
    val active: Boolean
)

data class WarehouseResponse(
    val id: Long?,
    val name: String,
    val active: Boolean,
    val createdAt: Instant?,
    val updatedAt: Instant?
)

data class WorkerCreateRequest(
    val firstName: String,
    val lastName: String,
    val phone: String,
    val password: String,
    val warehouseId: Long
)

data class WorkerUpdateRequest(
    val firstName: String,
    val lastName: String,
    val phone: String,
    val active: Boolean,
    val warehouseId: Long
)

data class WorkerResponse(
    val id: Long?,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val employeeCode: String,
    val warehouseId: Long,
    val warehouseName: String,
    val active: Boolean,
    val createdAt: Instant?,
    val updatedAt: Instant?
)

data class CategoryCreateRequest(
    val name: String,
    val parentId: Long? = null
)

data class CategoryUpdateRequest(
    val name: String,
    val active: Boolean,
    val parentId: Long? = null
)

data class CategoryResponse(
    val id: Long?,
    val name: String,
    val parentId: Long?,
    val parentName: String?,
    val active: Boolean
)

data class UnitCreateRequest(
    val name: String
)

data class UnitUpdateRequest(
    val name: String,
    val active: Boolean
)

data class UnitResponse(
    val id: Long?,
    val name: String,
    val active: Boolean
)

data class CurrencyCreateRequest(
    val name: String
)

data class CurrencyUpdateRequest(
    val name: String,
    val active: Boolean
)

data class CurrencyResponse(
    val id: Long?,
    val name: String,
    val active: Boolean
)

data class SupplierCreateRequest(
    val name: String,
    val phone: String
)

data class SupplierUpdateRequest(
    val name: String,
    val phone: String,
    val active: Boolean
)

data class SupplierResponse(
    val id: Long?,
    val name: String,
    val phone: String,
    val active: Boolean
)

/**
 * CHANGED: imageUrls bu yerda bo'lmaydi.
 * Product yaratish/yangilash rasmsiz bo'ladi, rasm alohida upload endpoint orqali keladi.
 */
data class ProductCreateRequest(
    val name: String,
    val categoryId: Long,
    val unitId: Long,
    val supplierId: Long,
    val currentSalePrice: BigDecimal? = null
)

/**
 * CHANGED: imageUrls bu yerda bo'lmaydi.
 * Rasmni yangilash/almashtirish ham alohida endpoint orqali qilinadi.
 */
data class ProductUpdateRequest(
    val name: String,
    val categoryId: Long,
    val unitId: Long,
    val supplierId: Long,
    val active: Boolean,
    val currentSalePrice: BigDecimal? = null
)

/**
 * ProductResponse'da imageUrls qoladi.
 * Bu url lar backend tomonidan product_images -> files orqali yig'ilib qaytariladi.
 */
data class ProductResponse(
    val id: Long?,
    val name: String,
    val categoryId: Long,
    val categoryName: String,
    val unitId: Long,
    val unitName: String,
    val supplierId: Long,
    val supplierName: String,
    val productCode: String,
    val imageUrls: List<String>,
    val currentSalePrice: BigDecimal,
    val active: Boolean
)

data class StockEntryItemCreateRequest(
    val productId: Long,
    val unitId: Long,
    val quantity: BigDecimal,
    val purchasePrice: BigDecimal,
    val salePrice: BigDecimal,
    val expiryDate: LocalDate?,
    val currencyId: Long
)

data class StockEntryCreateRequest(
    val date: LocalDate,
    val warehouseId: Long,
    val supplierId: Long,
    val invoiceNumber: String,
    val items: List<StockEntryItemCreateRequest>
)

data class StockEntryItemResponse(
    val id: Long?,
    val productId: Long,
    val productName: String,
    val unitId: Long,
    val unitName: String,
    val quantity: BigDecimal,
    val purchasePrice: BigDecimal,
    val salePrice: BigDecimal,
    val expiryDate: LocalDate?,
    val currencyId: Long,
    val currencyName: String
)

data class StockEntryResponse(
    val id: Long?,
    val date: LocalDate,
    val warehouseId: Long,
    val warehouseName: String,
    val supplierId: Long,
    val supplierName: String,
    val invoiceNumber: String,
    val entryCode: String,
    val items: List<StockEntryItemResponse>,
    val active: Boolean,
    val createdAt: Instant?,
    val updatedAt: Instant?
)

data class SaleItemCreateRequest(
    val productId: Long,
    val unitId: Long,
    val quantity: BigDecimal,
    val price: BigDecimal? = null,
    val currencyId: Long
)

data class SaleCreateRequest(
    val date: LocalDate,
    val warehouseId: Long,
    val invoiceNumber: String,
    val items: List<SaleItemCreateRequest>
)

data class SaleItemResponse(
    val id: Long?,
    val productId: Long,
    val productName: String,
    val unitId: Long,
    val unitName: String,
    val quantity: BigDecimal,
    val price: BigDecimal,
    val currencyId: Long,
    val currencyName: String
)

data class SaleResponse(
    val id: Long?,
    val date: LocalDate,
    val warehouseId: Long,
    val warehouseName: String,
    val invoiceNumber: String,
    val saleCode: String,
    val items: List<SaleItemResponse>,
    val active: Boolean,
    val createdAt: Instant?,
    val updatedAt: Instant?
)

data class ProductStockResponse(
    val id: Long?,
    val warehouseId: Long,
    val warehouseName: String,
    val productId: Long,
    val productName: String,
    val quantity: BigDecimal,
    val active: Boolean
)

data class DailyStockInProductDto(
    val productId: Long,
    val productName: String,
    val totalQuantity: BigDecimal,
    val unitName: String,
    val totalPurchaseAmount: BigDecimal,
    val currencyName: String
)

data class DailyStockInSummaryResponse(
    val date: LocalDate,
    val items: List<DailyStockInProductDto>
)

data class DailyTopSaleProductDto(
    val productId: Long,
    val productName: String,
    val totalQuantity: BigDecimal,
    val unitName: String,
    val totalSaleAmount: BigDecimal,
    val currencyName: String
)

data class DailyTopSalesResponse(
    val date: LocalDate,
    val items: List<DailyTopSaleProductDto>
)

data class ExpiringProductDto(
    val stockEntryItemId: Long,
    val productId: Long,
    val productName: String,
    val warehouseId: Long,
    val warehouseName: String,
    val quantity: BigDecimal,
    val unitName: String,
    val expiryDate: LocalDate,
    val daysLeft: Long
)

data class ExpiringProductsResponse(
    val asOfDate: LocalDate,
    val daysBeforeExpiry: Long,
    val totalCount: Long,
    val items: List<ExpiringProductDto>
)

data class NotificationSettingUpdateRequest(
    val key: String,
    val daysBefore: Long,
    val active: Boolean
)

data class NotificationSettingResponse(
    val id: Long?,
    val key: String,
    val daysBefore: Long,
    val active: Boolean
)


data class FileAssetResponse(
    val id: Long?,
    val url: String,
    val originalName: String,
    val contentType: String,
    val sizeBytes: Long,
    val createdAt: Instant?
)


data class ProductImageUploadResponse(
    val productId: Long,
    val uploaded: List<FileAssetResponse>
)
