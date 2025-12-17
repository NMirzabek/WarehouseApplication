package com.example.warehouseapplication

fun Warehouse.toResponse(): WarehouseResponse =
    WarehouseResponse(
        id = id,
        name = name,
        active = active,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

fun Worker.toResponse(): WorkerResponse =
    WorkerResponse(
        id = id,
        firstName = firstName,
        lastName = lastName,
        phone = phone,
        employeeCode = employeeCode,
        warehouseId = warehouse.id ?: 0L,
        warehouseName = warehouse.name,
        active = active,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

fun Category.toResponse(): CategoryResponse =
    CategoryResponse(
        id = id,
        name = name,
        parentId = parent?.id,
        parentName = parent?.name,
        active = active
    )

fun UnitOfMeasure.toResponse(): UnitResponse =
    UnitResponse(
        id = id,
        name = name,
        active = active
    )

fun Currency.toResponse(): CurrencyResponse =
    CurrencyResponse(
        id = id,
        name = name,
        active = active
    )

fun Supplier.toResponse(): SupplierResponse =
    SupplierResponse(
        id = id,
        name = name,
        phone = phone,
        active = active
    )

fun Product.toResponse(): ProductResponse =
    ProductResponse(
        id = id,
        name = name,
        categoryId = category.id ?: 0L,
        categoryName = category.name,
        unitId = unit.id ?: 0L,
        unitName = unit.name,
        supplierId = supplier.id ?: 0L,
        supplierName = supplier.name,
        productCode = productCode,
        imageUrls = images.map { "/files/${it.file.storageKey}" },
        currentSalePrice = currentSalePrice,
        active = active
    )

fun StockEntryItem.toResponse(): StockEntryItemResponse =
    StockEntryItemResponse(
        id = id,
        productId = product.id ?: 0L,
        productName = product.name,
        unitId = unit.id ?: 0L,
        unitName = unit.name,
        quantity = quantity,
        purchasePrice = purchasePrice,
        salePrice = salePrice,
        expiryDate = expiryDate,
        currencyId = currency.id ?: 0L,
        currencyName = currency.name
    )

fun StockEntry.toResponse(): StockEntryResponse =
    StockEntryResponse(
        id = id,
        date = date,
        warehouseId = warehouse.id ?: 0L,
        warehouseName = warehouse.name,
        supplierId = supplier.id ?: 0L,
        supplierName = supplier.name,
        invoiceNumber = invoiceNumber,
        entryCode = entryCode,
        items = items.map { it.toResponse() },
        active = active,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

fun SaleItem.toResponse(): SaleItemResponse =
    SaleItemResponse(
        id = id,
        productId = product.id ?: 0L,
        productName = product.name,
        unitId = unit.id ?: 0L,
        unitName = unit.name,
        quantity = quantity,
        price = price,
        currencyId = currency.id ?: 0L,
        currencyName = currency.name
    )

fun Sale.toResponse(): SaleResponse =
    SaleResponse(
        id = id,
        date = date,
        warehouseId = warehouse.id ?: 0L,
        warehouseName = warehouse.name,
        invoiceNumber = invoiceNumber,
        saleCode = saleCode,
        items = items.map { it.toResponse() },
        active = active,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

fun ProductStock.toResponse(): ProductStockResponse =
    ProductStockResponse(
        id = id,
        warehouseId = warehouse.id ?: 0L,
        warehouseName = warehouse.name,
        productId = product.id ?: 0L,
        productName = product.name,
        quantity = quantity,
        active = active
    )

fun NotificationSetting.toResponse(): NotificationSettingResponse =
    NotificationSettingResponse(
        id = id,
        key = key,
        daysBefore = daysBefore,
        active = active
    )

fun FileAsset.toResponse(): FileAssetResponse =
    FileAssetResponse(
        id = id,
        url = "/files/$storageKey",
        originalName = originalName,
        contentType = contentType,
        sizeBytes = sizeBytes,
        createdAt = createdAt
    )