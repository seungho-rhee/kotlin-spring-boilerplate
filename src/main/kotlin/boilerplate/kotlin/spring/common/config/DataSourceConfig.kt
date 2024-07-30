package boilerplate.kotlin.spring.common.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.*
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
import org.springframework.transaction.support.TransactionSynchronizationManager
import javax.sql.DataSource


class DataSourceRouter : AbstractRoutingDataSource() {
    override fun determineCurrentLookupKey(): Any {
        val readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly()
        val dataSource = if (readOnly) "read" else "write"
        return dataSource
    }
}

@Configuration
@Profile("!test")
class DataSourceConfig {
    // Write DataSource
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.write")
    fun writeDataSource(): DataSource {
        return DataSourceBuilder.create().type(HikariDataSource::class.java).build()
    }

    // Read DataSource
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.read")
    fun readDataSource(): DataSource {
        return DataSourceBuilder.create().type(HikariDataSource::class.java).build()
    }

    // 읽기 모드인지 여부로 DataSource를 분기 처리
    @Bean
    @DependsOn("writeDataSource", "readDataSource")
    fun routeDataSource(): DataSource {
        val dataSourceRouter = DataSourceRouter()
        val writeDataSource: DataSource = writeDataSource()
        val readDataSource: DataSource = readDataSource()

        val dataSourceMap = HashMap<Any, Any>()
        dataSourceMap["write"] = writeDataSource
        dataSourceMap["read"] = readDataSource
        dataSourceRouter.setTargetDataSources(dataSourceMap)
        dataSourceRouter.setDefaultTargetDataSource(writeDataSource)

        return dataSourceRouter
    }

    @Bean
    @Primary
    @DependsOn("routeDataSource")
    fun dataSource(): DataSource {
        return LazyConnectionDataSourceProxy(routeDataSource())
    }
}
