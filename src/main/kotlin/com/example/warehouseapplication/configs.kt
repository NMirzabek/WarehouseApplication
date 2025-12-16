package com.example.warehouseapplication

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.nio.file.Path

@Configuration
class WebConfig(
    @Value("\${app.upload-dir:uploads}")
    private val uploadDir: String
) : WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        val basePath = Path.of(uploadDir).toAbsolutePath().normalize()

        // MUHIM: Windows uchun to‘g‘ri "file:/C:/..." URI bo‘lishi kerak
        val location = basePath.toUri().toString() // masalan: file:/C:/project/uploads/

        registry.addResourceHandler("/files/**")
            .addResourceLocations(location)
            .setCachePeriod(3600)
    }
}
