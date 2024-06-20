package boilerplate.kotlin.spring.common.filter

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class AuthenticateFilter : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(this::class.java)
    companion object {
        val SERVER_API_KEY: Map<String, String> = mapOf(
            "i0k8J2CsUsdfeZBAWsY4vwskSyrJ5v" to "test-client",
        )

        val API_KEY_HEADER = "API-KEY"
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val server = request.getHeader(API_KEY_HEADER)
            ?.ifBlank { null }
            ?.let { SERVER_API_KEY[it] }
            ?: return writeTokenErrorResponse(response)

        MDC.put("server", server)

        filterChain.doFilter(request, response)
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.servletPath
        return path.contains("/actuator")
    }

    @Throws(IOException::class)
    private fun writeTokenErrorResponse(response: HttpServletResponse) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE

        response.outputStream.use { os ->
            ObjectMapper().writeValue(os, InvalidKeyResponse(
                status = HttpStatus.UNAUTHORIZED.value(),
                message = "unauthorized"
            ))
            os.flush()
        }
    }

    inner class InvalidKeyResponse(
        val status: Int,
        val message: String,
    )

}