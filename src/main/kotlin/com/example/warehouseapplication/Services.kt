package com.example.warehouseapplication

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.io.path.createDirectories

@Service
class WarehouseService(
    private val warehouseRepository: WarehouseRepository
) {
    @Transactional
    fun create(request: WarehouseCreateRequest): ApiResponse<WarehouseResponse> {
        val warehouse = Warehouse(name = request.name)
        val saved = warehouseRepository.save(warehouse)

        return ApiResponse(
            success = true,
            message = "Warehouse created",
            data = saved.toResponse()
        )
    }

    @Transactional
    fun update(id: Long, request: WarehouseUpdateRequest): ApiResponse<WarehouseResponse> {
        val warehouse = warehouseRepository.findById(id)
            .orElseThrow { NotFoundException("Warehouse not found with id=$id") }

        warehouse.name = request.name
        warehouse.active = request.active

        val saved = warehouseRepository.save(warehouse)

        return ApiResponse(
            success = true,
            message = "Warehouse updated",
            data = saved.toResponse()
        )
    }

    @Transactional(readOnly = true)
    fun getById(id: Long): ApiResponse<WarehouseResponse> {
        val warehouse = warehouseRepository.findById(id)
            .orElseThrow { NotFoundException("Warehouse not found with id=$id") }

        return ApiResponse(success = true, data = warehouse.toResponse())
    }

    @Transactional(readOnly = true)
    fun getAll(): ApiResponse<List<WarehouseResponse>> =
        ApiResponse(success = true, data = warehouseRepository.findAll().map { it.toResponse() })

    @Transactional(readOnly = true)
    fun getAllActive(): ApiResponse<List<WarehouseResponse>> =
        ApiResponse(success = true, data = warehouseRepository.findAllByActiveTrue().map { it.toResponse() })
}

@Service
class WorkerService(
    private val workerRepository: WorkerRepository,
    private val warehouseRepository: WarehouseRepository
) {
    @Transactional
    fun create(request: WorkerCreateRequest): ApiResponse<WorkerResponse> {
        val warehouse = warehouseRepository.findById(request.warehouseId)
            .orElseThrow { NotFoundException("Warehouse not found with id=${request.warehouseId}") }

        val employeeCode = generateEmployeeCode()

        val worker = Worker(
            firstName = request.firstName,
            lastName = request.lastName,
            phone = request.phone,
            employeeCode = employeeCode,
            passwordHash = request.password,
            warehouse = warehouse
        )

        val saved = workerRepository.save(worker)
        return ApiResponse(success = true, message = "Worker created", data = saved.toResponse())
    }

    @Transactional
    fun update(id: Long, request: WorkerUpdateRequest): ApiResponse<WorkerResponse> {
        val worker = workerRepository.findById(id)
            .orElseThrow { NotFoundException("Worker not found with id=$id") }

        val warehouse = warehouseRepository.findById(request.warehouseId)
            .orElseThrow { NotFoundException("Warehouse not found with id=${request.warehouseId}") }

        worker.firstName = request.firstName
        worker.lastName = request.lastName
        worker.phone = request.phone
        worker.active = request.active
        worker.warehouse = warehouse

        val saved = workerRepository.save(worker)
        return ApiResponse(success = true, message = "Worker updated", data = saved.toResponse())
    }

    @Transactional(readOnly = true)
    fun getById(id: Long): ApiResponse<WorkerResponse> {
        val worker = workerRepository.findById(id)
            .orElseThrow { NotFoundException("Worker not found with id=$id") }

        return ApiResponse(success = true, data = worker.toResponse())
    }

    @Transactional(readOnly = true)
    fun getAllActive(): ApiResponse<List<WorkerResponse>> =
        ApiResponse(success = true, data = workerRepository.findAllByActiveTrue().map { it.toResponse() })

    private fun generateEmployeeCode(): String {
        val timestamp = Instant.now().epochSecond
        val randomPart = (100..999).random()
        return "W-$timestamp-$randomPart"
    }
}

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository
) {
    @Transactional
    fun create(request: CategoryCreateRequest): ApiResponse<CategoryResponse> {
        val parent = request.parentId?.let { pid ->
            categoryRepository.findById(pid)
                .orElseThrow { NotFoundException("Parent category not found with id=$pid") }
        }

        val category = Category(name = request.name, parent = parent)
        val saved = categoryRepository.save(category)

        return ApiResponse(success = true, message = "Category created", data = saved.toResponse())
    }

    @Transactional
    fun update(id: Long, request: CategoryUpdateRequest): ApiResponse<CategoryResponse> {
        val category = categoryRepository.findById(id)
            .orElseThrow { NotFoundException("Category not found with id=$id") }

        val parent = request.parentId?.let { pid ->
            if (pid == id) throw BusinessException("Category cannot be parent of itself")
            categoryRepository.findById(pid)
                .orElseThrow { NotFoundException("Parent category not found with id=$pid") }
        }

        category.name = request.name
        category.active = request.active
        category.parent = parent

        val saved = categoryRepository.save(category)
        return ApiResponse(success = true, message = "Category updated", data = saved.toResponse())
    }

    @Transactional(readOnly = true)
    fun getAll(): ApiResponse<List<CategoryResponse>> =
        ApiResponse(success = true, data = categoryRepository.findAll().map { it.toResponse() })

    @Transactional(readOnly = true)
    fun getAllActive(): ApiResponse<List<CategoryResponse>> =
        ApiResponse(success = true, data = categoryRepository.findAllByActiveTrue().map { it.toResponse() })
}

@Service
class UnitService(
    private val unitRepository: UnitOfMeasureRepository
) {
    @Transactional
    fun create(request: UnitCreateRequest): ApiResponse<UnitResponse> {
        val unit = UnitOfMeasure(name = request.name)
        val saved = unitRepository.save(unit)
        return ApiResponse(success = true, message = "Unit created", data = saved.toResponse())
    }

    @Transactional
    fun update(id: Long, request: UnitUpdateRequest): ApiResponse<UnitResponse> {
        val unit = unitRepository.findById(id)
            .orElseThrow { NotFoundException("Unit not found with id=$id") }

        unit.name = request.name
        unit.active = request.active

        val saved = unitRepository.save(unit)
        return ApiResponse(success = true, message = "Unit updated", data = saved.toResponse())
    }

    @Transactional(readOnly = true)
    fun getAllActive(): ApiResponse<List<UnitResponse>> =
        ApiResponse(success = true, data = unitRepository.findAllByActiveTrue().map { it.toResponse() })
}

@Service
class CurrencyService(
    private val currencyRepository: CurrencyRepository
) {
    private val allowedCurrency = "UZS"

    @Transactional
    fun create(request: CurrencyCreateRequest): ApiResponse<CurrencyResponse> {
        val incoming = request.name.trim().uppercase()
        if (incoming != allowedCurrency) throw BusinessException("Only $allowedCurrency currency is allowed")

        val currency = Currency(name = allowedCurrency)
        val saved = currencyRepository.save(currency)

        return ApiResponse(success = true, message = "Currency created", data = saved.toResponse())
    }

    @Transactional
    fun update(id: Long, request: CurrencyUpdateRequest): ApiResponse<CurrencyResponse> {
        val currency = currencyRepository.findById(id)
            .orElseThrow { NotFoundException("Currency not found with id=$id") }

        val incoming = request.name.trim().uppercase()
        if (incoming != allowedCurrency) throw BusinessException("Only $allowedCurrency currency is allowed")

        currency.name = allowedCurrency
        currency.active = request.active

        val saved = currencyRepository.save(currency)
        return ApiResponse(success = true, message = "Currency updated", data = saved.toResponse())
    }

    @Transactional(readOnly = true)
    fun getAllActive(): ApiResponse<List<CurrencyResponse>> =
        ApiResponse(success = true, data = currencyRepository.findAllByActiveTrue().map { it.toResponse() })
}

@Service
class SupplierService(
    private val supplierRepository: SupplierRepository
) {
    @Transactional
    fun create(request: SupplierCreateRequest): ApiResponse<SupplierResponse> {
        val supplier = Supplier(name = request.name, phone = request.phone)
        val saved = supplierRepository.save(supplier)
        return ApiResponse(success = true, message = "Supplier created", data = saved.toResponse())
    }

    @Transactional
    fun update(id: Long, request: SupplierUpdateRequest): ApiResponse<SupplierResponse> {
        val supplier = supplierRepository.findById(id)
            .orElseThrow { NotFoundException("Supplier not found with id=$id") }

        supplier.name = request.name
        supplier.phone = request.phone
        supplier.active = request.active

        val saved = supplierRepository.save(supplier)
        return ApiResponse(success = true, message = "Supplier updated", data = saved.toResponse())
    }

    @Transactional(readOnly = true)
    fun getAllActive(): ApiResponse<List<SupplierResponse>> =
        ApiResponse(success = true, data = supplierRepository.findAllByActiveTrue().map { it.toResponse() })
}

@Service
class ProductStockService(
    private val productStockRepository: ProductStockRepository
) {
    @Transactional
    fun increaseStock(warehouse: Warehouse, product: Product, quantity: BigDecimal) {
        val stock = productStockRepository.findByWarehouseIdAndProductId(
            warehouseId = warehouse.id ?: 0L,
            productId = product.id ?: 0L
        ) ?: ProductStock(
            warehouse = warehouse,
            product = product,
            quantity = BigDecimal.ZERO
        )

        stock.quantity = stock.quantity.add(quantity)
        productStockRepository.save(stock)
    }

    @Transactional
    fun decreaseStock(warehouse: Warehouse, product: Product, quantity: BigDecimal) {
        val stock = productStockRepository.findByWarehouseIdAndProductId(
            warehouseId = warehouse.id ?: 0L,
            productId = product.id ?: 0L
        ) ?: throw BusinessException("No stock for product=${product.id} in warehouse=${warehouse.id}")

        if (stock.quantity < quantity) {
            throw BusinessException("Not enough stock for product=${product.id} in warehouse=${warehouse.id}")
        }

        stock.quantity = stock.quantity.subtract(quantity)
        productStockRepository.save(stock)
    }
}

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val unitRepository: UnitOfMeasureRepository,
    private val supplierRepository: SupplierRepository
) {
    @Transactional
    fun create(request: ProductCreateRequest): ApiResponse<ProductResponse> {
        val category = categoryRepository.findById(request.categoryId)
            .orElseThrow { NotFoundException("Category not found with id=${request.categoryId}") }

        val unit = unitRepository.findById(request.unitId)
            .orElseThrow { NotFoundException("Unit not found with id=${request.unitId}") }

        val supplier = supplierRepository.findById(request.supplierId)
            .orElseThrow { NotFoundException("Supplier not found with id=${request.supplierId}") }

        val product = Product(
            name = request.name,
            category = category,
            productCode = generateProductCode(),
            unit = unit,
            supplier = supplier
        ).also { p ->
            p.currentSalePrice = request.currentSalePrice ?: BigDecimal.ZERO
        }

        val saved = productRepository.save(product)
        return ApiResponse(success = true, message = "Product created", data = saved.toResponse())
    }

    @Transactional
    fun update(id: Long, request: ProductUpdateRequest): ApiResponse<ProductResponse> {
        val product = productRepository.findById(id)
            .orElseThrow { NotFoundException("Product not found with id=$id") }

        val category = categoryRepository.findById(request.categoryId)
            .orElseThrow { NotFoundException("Category not found with id=${request.categoryId}") }

        val unit = unitRepository.findById(request.unitId)
            .orElseThrow { NotFoundException("Unit not found with id=${request.unitId}") }

        val supplier = supplierRepository.findById(request.supplierId)
            .orElseThrow { NotFoundException("Supplier not found with id=${request.supplierId}") }

        product.name = request.name
        product.category = category
        product.unit = unit
        product.supplier = supplier
        product.active = request.active

        request.currentSalePrice?.let { product.currentSalePrice = it }

        val saved = productRepository.save(product)
        return ApiResponse(success = true, message = "Product updated", data = saved.toResponse())
    }

    @Transactional(readOnly = true)
    fun getById(id: Long): ApiResponse<ProductResponse> {
        val product = productRepository.findById(id)
            .orElseThrow { NotFoundException("Product not found with id=$id") }
        return ApiResponse(success = true, data = product.toResponse())
    }

    @Transactional(readOnly = true)
    fun getAllActive(): ApiResponse<List<ProductResponse>> =
        ApiResponse(success = true, data = productRepository.findAllByActiveTrue().map { it.toResponse() })

    private fun generateProductCode(): String {
        val timestamp = Instant.now().epochSecond
        val randomPart = (1000..9999).random()
        return "P-$timestamp-$randomPart"
    }
}

@Service
class FileStorageService(
    @Value("\${app.upload-dir:uploads}")
    private val uploadDir: String
) {
    fun saveProductFile(productId: Long, file: MultipartFile): String {
        if (file.isEmpty) throw BusinessException("File is empty")

        val safeOriginal = (file.originalFilename ?: "file").replace("\\s+".toRegex(), "_")
        val ext = safeOriginal.substringAfterLast('.', "").let { if (it.isBlank()) "bin" else it.lowercase() }

        val filename = "${Instant.now().toEpochMilli()}_${productId}.$ext"
        val relativeKey = "products/$productId/$filename"

        val basePath = Path.of(uploadDir).normalize().toAbsolutePath()
        val targetPath = basePath.resolve(relativeKey).normalize()

        targetPath.parent.createDirectories()

        Files.copy(file.inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING)
        return relativeKey
    }
}

@Service
class FileAssetService(
    private val fileAssetRepository: FileAssetRepository
) {
    @Transactional
    fun create(storageKey: String, originalName: String, contentType: String, sizeBytes: Long): FileAsset {
        val asset = FileAsset(
            storageKey = storageKey,
            originalName = originalName,
            contentType = contentType,
            sizeBytes = sizeBytes
        )
        return fileAssetRepository.save(asset)
    }
}

@Service
class ProductImageService(
    private val productRepository: ProductRepository,
    private val productImageRepository: ProductImageRepository
) {
    @Transactional
    fun link(productId: Long, fileAsset: FileAsset): ProductImage {
        val product = productRepository.findById(productId)
            .orElseThrow { NotFoundException("Product not found with id=$productId") }

        val link = ProductImage(
            product = product,
            file = fileAsset
        )
        return productImageRepository.save(link)
    }
}

@Service
class ProductImageUploadService(
    private val fileStorageService: FileStorageService,
    private val fileAssetService: FileAssetService,
    private val productImageService: ProductImageService
) {
    @Transactional
    fun upload(productId: Long, files: List<MultipartFile>): ApiResponse<ProductImageUploadResponse> {
        if (files.isEmpty()) throw BusinessException("No files provided")

        val uploaded = files.map { file ->
            val storageKey = fileStorageService.saveProductFile(productId, file)

            val savedAsset = fileAssetService.create(
                storageKey = storageKey,
                originalName = file.originalFilename ?: "file",
                contentType = file.contentType ?: "application/octet-stream",
                sizeBytes = file.size
            )

            productImageService.link(productId, savedAsset)

            savedAsset.toResponse()
        }

        return ApiResponse(
            success = true,
            message = "Images uploaded",
            data = ProductImageUploadResponse(productId = productId, uploaded = uploaded)
        )
    }
}

@Service
class StockEntryService(
    private val stockEntryRepository: StockEntryRepository,
    private val warehouseRepository: WarehouseRepository,
    private val supplierRepository: SupplierRepository,
    private val productRepository: ProductRepository,
    private val unitRepository: UnitOfMeasureRepository,
    private val currencyRepository: CurrencyRepository,
    private val productStockService: ProductStockService
) {
    @Transactional
    fun create(request: StockEntryCreateRequest): ApiResponse<StockEntryResponse> {
        val warehouse = warehouseRepository.findById(request.warehouseId)
            .orElseThrow { NotFoundException("Warehouse not found with id=${request.warehouseId}") }

        val supplier = supplierRepository.findById(request.supplierId)
            .orElseThrow { NotFoundException("Supplier not found with id=${request.supplierId}") }

        val entry = StockEntry(
            date = request.date,
            warehouse = warehouse,
            supplier = supplier,
            invoiceNumber = request.invoiceNumber,
            entryCode = generateEntryCode()
        )

        val items = request.items.map { itemReq ->
            val product = productRepository.findById(itemReq.productId)
                .orElseThrow { NotFoundException("Product not found with id=${itemReq.productId}") }

            val unit = unitRepository.findById(itemReq.unitId)
                .orElseThrow { NotFoundException("Unit not found with id=${itemReq.unitId}") }

            val currency = currencyRepository.findById(itemReq.currencyId)
                .orElseThrow { NotFoundException("Currency not found with id=${itemReq.currencyId}") }

            StockEntryItem(
                stockEntry = entry,
                product = product,
                unit = unit,
                quantity = itemReq.quantity,
                purchasePrice = itemReq.purchasePrice,
                salePrice = itemReq.salePrice,
                expiryDate = itemReq.expiryDate,
                currency = currency
            )
        }

        entry.items.addAll(items)

        val saved = stockEntryRepository.save(entry)

        saved.items.forEach { item ->
            productStockService.increaseStock(saved.warehouse, item.product, item.quantity)
        }

        val affectedProducts = saved.items.map { it.product }.distinctBy { it.id }
        affectedProducts.forEach { p ->
            val lastPrice = saved.items.lastOrNull { it.product.id == p.id }?.salePrice
            if (lastPrice != null) p.currentSalePrice = lastPrice
        }
        productRepository.saveAll(affectedProducts)

        return ApiResponse(success = true, message = "Stock entry created", data = saved.toResponse())
    }

    @Transactional(readOnly = true)
    fun getById(id: Long): ApiResponse<StockEntryResponse> {
        val entry = stockEntryRepository.findById(id)
            .orElseThrow { NotFoundException("Stock entry not found with id=$id") }
        return ApiResponse(success = true, data = entry.toResponse())
    }

    private fun generateEntryCode(): String {
        val timestamp = Instant.now().epochSecond
        val randomPart = (1000..9999).random()
        return "IN-$timestamp-$randomPart"
    }
}

@Service
class SaleService(
    private val saleRepository: SaleRepository,
    private val warehouseRepository: WarehouseRepository,
    private val productRepository: ProductRepository,
    private val unitRepository: UnitOfMeasureRepository,
    private val currencyRepository: CurrencyRepository,
    private val productStockService: ProductStockService
) {
    @Transactional
    fun create(request: SaleCreateRequest): ApiResponse<SaleResponse> {
        val warehouse = warehouseRepository.findById(request.warehouseId)
            .orElseThrow { NotFoundException("Warehouse not found with id=${request.warehouseId}") }

        val sale = Sale(
            date = request.date,
            warehouse = warehouse,
            invoiceNumber = request.invoiceNumber,
            saleCode = generateSaleCode()
        )

        val items = request.items.map { itemReq ->
            val product = productRepository.findById(itemReq.productId)
                .orElseThrow { NotFoundException("Product not found with id=${itemReq.productId}") }

            val unit = unitRepository.findById(itemReq.unitId)
                .orElseThrow { NotFoundException("Unit not found with id=${itemReq.unitId}") }

            val currency = currencyRepository.findById(itemReq.currencyId)
                .orElseThrow { NotFoundException("Currency not found with id=${itemReq.currencyId}") }

            val actualPrice = itemReq.price ?: product.currentSalePrice
            if (actualPrice <= BigDecimal.ZERO) {
                throw BusinessException(
                    "Sale price is not set for product=${product.id}. " +
                            "Provide item.price or set product currentSalePrice via stock entry."
                )
            }

            SaleItem(
                sale = sale,
                product = product,
                unit = unit,
                quantity = itemReq.quantity,
                price = actualPrice,
                currency = currency
            )
        }

        sale.items.addAll(items)

        val saved = saleRepository.save(sale)

        saved.items.forEach { item ->
            productStockService.decreaseStock(saved.warehouse, item.product, item.quantity)
        }

        return ApiResponse(success = true, message = "Sale created", data = saved.toResponse())
    }

    @Transactional(readOnly = true)
    fun getById(id: Long): ApiResponse<SaleResponse> {
        val sale = saleRepository.findById(id)
            .orElseThrow { NotFoundException("Sale not found with id=$id") }
        return ApiResponse(success = true, data = sale.toResponse())
    }

    private fun generateSaleCode(): String {
        val timestamp = Instant.now().epochSecond
        val randomPart = (1000..9999).random()
        return "OUT-$timestamp-$randomPart"
    }
}

@Service
class DashboardService(
    private val stockEntryItemRepository: StockEntryItemRepository,
    private val saleItemRepository: SaleItemRepository
) {
    @Transactional(readOnly = true)
    fun getDailyStockInSummary(date: LocalDate): ApiResponse<DailyStockInSummaryResponse> {
        val items = stockEntryItemRepository.findActiveItemsByEntryDate(date)

        val grouped = items.groupBy { it.product.id to it.currency.id }

        val dtos = grouped.map { (_, groupItems) ->
            val first = groupItems.first()

            val totalQty = groupItems.fold(BigDecimal.ZERO) { acc, i -> acc.add(i.quantity) }
            val totalAmount = groupItems.fold(BigDecimal.ZERO) { acc, i ->
                acc.add(i.quantity.multiply(i.purchasePrice))
            }

            DailyStockInProductDto(
                productId = first.product.id ?: 0L,
                productName = first.product.name,
                totalQuantity = totalQty,
                unitName = first.unit.name,
                totalPurchaseAmount = totalAmount,
                currencyName = first.currency.name
            )
        }

        return ApiResponse(success = true, data = DailyStockInSummaryResponse(date = date, items = dtos))
    }

    @Transactional(readOnly = true)
    fun getDailyTopSales(date: LocalDate): ApiResponse<DailyTopSalesResponse> {
        val items = saleItemRepository.findActiveItemsBySaleDate(date)

        val grouped = items.groupBy { it.product.id to it.currency.id }

        val dtos = grouped.map { (_, groupItems) ->
            val first = groupItems.first()

            val totalQty = groupItems.fold(BigDecimal.ZERO) { acc, i -> acc.add(i.quantity) }
            val totalAmount = groupItems.fold(BigDecimal.ZERO) { acc, i ->
                acc.add(i.quantity.multiply(i.price))
            }

            DailyTopSaleProductDto(
                productId = first.product.id ?: 0L,
                productName = first.product.name,
                totalQuantity = totalQty,
                unitName = first.unit.name,
                totalSaleAmount = totalAmount,
                currencyName = first.currency.name
            )
        }

        return ApiResponse(success = true, data = DailyTopSalesResponse(date = date, items = dtos))
    }
}

@Service
class NotificationSettingService(
    private val notificationSettingRepository: NotificationSettingRepository
) {
    private val defaultKey = "EXPIRY_DAYS_BEFORE"

    @Transactional
    fun getCurrent(): ApiResponse<NotificationSettingResponse> {
        val setting = notificationSettingRepository.findByKey(defaultKey)
            ?: notificationSettingRepository.save(NotificationSetting(key = defaultKey, daysBefore = 7))

        return ApiResponse(success = true, data = setting.toResponse())
    }

    @Transactional
    fun update(request: NotificationSettingUpdateRequest): ApiResponse<NotificationSettingResponse> {
        if (request.key != defaultKey) throw BusinessException("Unsupported notification key: ${request.key}")

        val setting = notificationSettingRepository.findByKey(defaultKey)?.apply {
            daysBefore = request.daysBefore
            active = request.active
        } ?: NotificationSetting(key = defaultKey, daysBefore = request.daysBefore).also {
            it.active = request.active
        }

        val saved = notificationSettingRepository.save(setting)
        return ApiResponse(success = true, message = "Notification setting updated", data = saved.toResponse())
    }
}

@Service
class TelegramNotificationService(
    @Value("\${telegram.bot-token}")
    private val botToken: String,
    @Value("\${telegram.chat-id}")
    private val chatId: String
) {
    private val restTemplate = RestTemplate()

    private val baseUrl: String
        get() = "https://api.telegram.org/bot$botToken"

    fun sendMessage(text: String) {
        val url = "$baseUrl/sendMessage"
        val payload = mapOf("chat_id" to chatId, "text" to text)
        restTemplate.postForObject(url, payload, String::class.java)
    }

    fun getDefaultChatId(): String = chatId
}

@Service
class ExpiryCheckService(
    private val stockEntryItemRepository: StockEntryItemRepository,
    private val expiryNotificationRepository: ExpiryNotificationRepository,
    private val notificationSettingRepository: NotificationSettingRepository,
    private val telegramNotificationService: TelegramNotificationService
) {
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    fun checkAndNotifyExpiringProducts() {
        val today = LocalDate.now()

        val daysBefore = notificationSettingRepository.findByKey("EXPIRY_DAYS_BEFORE")
            ?.takeIf { it.active }
            ?.daysBefore ?: 7L

        val toDate = today.plusDays(daysBefore)

        val items = stockEntryItemRepository.findItemsExpiringBetween(today, toDate)
        if (items.isEmpty()) return

        val chatId = telegramNotificationService.getDefaultChatId()

        items.forEach { item ->
            val itemId = item.id ?: return@forEach
            val expiryDate = item.expiryDate ?: return@forEach

            val alreadyNotified = expiryNotificationRepository.existsByStockEntryItemIdAndChatId(itemId, chatId)
            if (alreadyNotified) return@forEach

            val daysLeft = ChronoUnit.DAYS.between(today, expiryDate)

            val message = buildString {
                appendLine("⚠️ Yaroqlilik muddati yaqinlashmoqda!")
                appendLine()
                appendLine("Mahsulot: ${item.product.name}")
                appendLine("Ombor: ${item.stockEntry.warehouse.name}")
                appendLine("Miqdor: ${item.quantity} ${item.unit.name}")
                appendLine("Tugash sanasi: $expiryDate")
                appendLine("Qolgan kunlar: $daysLeft")
            }

            telegramNotificationService.sendMessage(message)

            expiryNotificationRepository.save(
                ExpiryNotification(
                    stockEntryItem = item,
                    chatId = chatId,
                    sentAt = Instant.now()
                )
            )
        }
    }
}

@Service
open class AuthService(
    private val workerRepository: WorkerRepository,
    private val warehouseRepository: WarehouseRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {

    open fun login(req: LoginRequest): ApiResponse<LoginResponse> {
        val worker = workerRepository.findByEmployeeCodeAndActiveTrue(req.employeeCode)
            ?: return ApiResponse(success = false, message = "Invalid credentials")

        if (!passwordEncoder.matches(req.password, worker.passwordHash)) {
            return ApiResponse(success = false, message = "Invalid credentials")
        }

        val token = jwtService.generate(worker)
        return ApiResponse(
            success = true,
            message = "Login successful",
            data = LoginResponse(
                token = token,
                workerId = worker.id!!,
                employeeCode = worker.employeeCode,
                role = worker.role.name,
                warehouseId = worker.warehouse.id!!
            )
        )
    }

    open fun register(req: RegisterRequest): ApiResponse<RegisterResponse> {
        val warehouse = warehouseRepository.findById(req.warehouseId)
            .orElseThrow { RuntimeException("Warehouse not found") }

        val employeeCode = generateEmployeeCode()

        val worker = Worker(
            firstName = req.firstName.trim(),
            lastName = req.lastName.trim(),
            phone = req.phone.trim(),
            employeeCode = employeeCode,
            passwordHash = passwordEncoder.encode(req.password),
            role = req.role ?: Role.WORKER,
            warehouse = warehouse
        )

        val saved = workerRepository.save(worker)

        return ApiResponse(
            success = true,
            message = "Registered successfully",
            data = RegisterResponse(
                workerId = saved.id!!,
                employeeCode = saved.employeeCode,
                role = saved.role.name
            )
        )
    }

    private fun generateEmployeeCode(): String {
        while (true) {
            val code = "EMP" + (100000..999999).random()
            if (!workerRepository.existsByEmployeeCode(code)) return code
        }
    }
}