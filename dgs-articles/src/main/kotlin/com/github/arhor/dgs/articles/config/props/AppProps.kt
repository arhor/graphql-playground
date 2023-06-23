package com.github.arhor.dgs.articles.config.props

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "application-props")
data class AppProps(
    val aws: Aws,
) {
    data class Aws(
        val sns: Sns,
    ) {
        data class Sns(
            val articleUpdatedEvents: String,
            val articleDeletedEvents: String,
        )
    }
}