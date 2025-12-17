package com.example.warehouseapplication

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/warehouses")
class WarehousesController(
    private val warehouseService: WarehouseService
) {
    @PostMapping
    fun create(@RequestBody request: WarehouseCreateRequest): ResponseEntity<ApiResponse<WarehouseResponse>> =
        ResponseEntity.status(HttpStatus.CREATED).body(warehouseService.create(request))

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: WarehouseUpdateRequest): ApiResponse<WarehouseResponse> =
        warehouseService.update(id, request)

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ApiResponse<WarehouseResponse> =
        warehouseService.getById(id)

    @GetMapping
    fun getAll(): ApiResponse<List<WarehouseResponse>> =
        warehouseService.getAll()

    @GetMapping("/active")
    fun getAllActive(): ApiResponse<List<WarehouseResponse>> =
        warehouseService.getAllActive()
}

@RestController
@RequestMapping("/api/v1/workers")
class WorkersController(
    private val workerService: WorkerService
) {
    @PostMapping
    fun create(@RequestBody request: WorkerCreateRequest): ResponseEntity<ApiResponse<WorkerResponse>> =
        ResponseEntity.status(HttpStatus.CREATED).body(workerService.create(request))

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: WorkerUpdateRequest): ApiResponse<WorkerResponse> =
        workerService.update(id, request)

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ApiResponse<WorkerResponse> =
        workerService.getById(id)

    @GetMapping("/active")
    fun getAllActive(): ApiResponse<List<WorkerResponse>> =
        workerService.getAllActive()
}

@RestController
@RequestMapping("/api/v1/categories")
class CategoriesController(
    private val categoryService: CategoryService
) {
    @PostMapping
    fun create(@RequestBody request: CategoryCreateRequest): ResponseEntity<ApiResponse<CategoryResponse>> =
        ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(request))

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: CategoryUpdateRequest): ApiResponse<CategoryResponse> =
        categoryService.update(id, request)

    @GetMapping
    fun getAll(): ApiResponse<List<CategoryResponse>> =
        categoryService.getAll()

    @GetMapping("/active")
    fun getAllActive(): ApiResponse<List<CategoryResponse>> =
        categoryService.getAllActive()
}

@RestController
@RequestMapping("/api/v1/units")
class UnitsController(
    private val unitService: UnitService
) {
    @PostMapping
    fun create(@RequestBody request: UnitCreateRequest): ResponseEntity<ApiResponse<UnitResponse>> =
        ResponseEntity.status(HttpStatus.CREATED).body(unitService.create(request))

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: UnitUpdateRequest): ApiResponse<UnitResponse> =
        unitService.update(id, request)

    @GetMapping("/active")
    fun getAllActive(): ApiResponse<List<UnitResponse>> =
        unitService.getAllActive()
}

@RestController
@RequestMapping("/api/v1/currencies")
class CurrenciesController(
    private val currencyService: CurrencyService
) {
    @PostMapping
    fun create(@RequestBody request: CurrencyCreateRequest): ResponseEntity<ApiResponse<CurrencyResponse>> =
        ResponseEntity.status(HttpStatus.CREATED).body(currencyService.create(request))

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: CurrencyUpdateRequest): ApiResponse<CurrencyResponse> =
        currencyService.update(id, request)

    @GetMapping("/active")
    fun getAllActive(): ApiResponse<List<CurrencyResponse>> =
        currencyService.getAllActive()
}

@RestController
@RequestMapping("/api/v1/suppliers")
class SuppliersController(
    private val supplierService: SupplierService
) {
    @PostMapping
    fun create(@RequestBody request: SupplierCreateRequest): ResponseEntity<ApiResponse<SupplierResponse>> =
        ResponseEntity.status(HttpStatus.CREATED).body(supplierService.create(request))

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: SupplierUpdateRequest): ApiResponse<SupplierResponse> =
        supplierService.update(id, request)

    @GetMapping("/active")
    fun getAllActive(): ApiResponse<List<SupplierResponse>> =
        supplierService.getAllActive()
}

@RestController
@RequestMapping("/api/v1/products")
class ProductsController(
    private val productService: ProductService,
    private val productImageUploadService: ProductImageUploadService
) {
    @PostMapping
    fun create(@RequestBody request: ProductCreateRequest): ResponseEntity<ApiResponse<ProductResponse>> =
        ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request))

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: ProductUpdateRequest): ApiResponse<ProductResponse> =
        productService.update(id, request)

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ApiResponse<ProductResponse> =
        productService.getById(id)

    @GetMapping("/active")
    fun getAllActive(): ApiResponse<List<ProductResponse>> =
        productService.getAllActive()


    @PostMapping(
        "/{productId}/images",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun uploadImages(
        @PathVariable productId: Long,
        @RequestParam("files") files: List<MultipartFile>
    ): ApiResponse<ProductImageUploadResponse> =
        productImageUploadService.upload(productId, files)
}

@RestController
@RequestMapping("/api/v1/stock-entries")
class StockEntriesController(
    private val stockEntryService: StockEntryService
) {
    @PostMapping
    fun create(@RequestBody request: StockEntryCreateRequest): ResponseEntity<ApiResponse<StockEntryResponse>> =
        ResponseEntity.status(HttpStatus.CREATED).body(stockEntryService.create(request))

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ApiResponse<StockEntryResponse> =
        stockEntryService.getById(id)
}

@RestController
@RequestMapping("/api/v1/sales")
class SalesController(
    private val saleService: SaleService
) {
    @PostMapping
    fun create(@RequestBody request: SaleCreateRequest): ResponseEntity<ApiResponse<SaleResponse>> =
        ResponseEntity.status(HttpStatus.CREATED).body(saleService.create(request))

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ApiResponse<SaleResponse> =
        saleService.getById(id)
}

@RestController
@RequestMapping("/api/v1/dashboard")
class DashboardController(
    private val dashboardService: DashboardService
) {
    @GetMapping("/stock-in")
    fun dailyStockIn(@RequestParam("date") date: String): ApiResponse<DailyStockInSummaryResponse> =
        dashboardService.getDailyStockInSummary(LocalDate.parse(date))

    @GetMapping("/sales")
    fun dailySales(@RequestParam("date") date: String): ApiResponse<DailyTopSalesResponse> =
        dashboardService.getDailyTopSales(LocalDate.parse(date))
}

@RestController
@RequestMapping("/api/v1/notification-settings")
class NotificationSettingsController(
    private val notificationSettingService: NotificationSettingService
) {
    @GetMapping
    fun getCurrent(): ApiResponse<NotificationSettingResponse> =
        notificationSettingService.getCurrent()

    @PutMapping
    fun update(@RequestBody request: NotificationSettingUpdateRequest): ApiResponse<NotificationSettingResponse> =
        notificationSettingService.update(request)
}

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/login")
    fun login(@RequestBody req: LoginRequest): ApiResponse<LoginResponse> =
        authService.login(req)

    @PostMapping("/register")
    fun register(@RequestBody req: RegisterRequest): ApiResponse<RegisterResponse> =
        authService.register(req)
}
