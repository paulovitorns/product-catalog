package br.com.productcatalog.domain

import org.junit.Test

class CleanQueryStringUseCaseTest {

    private val verifyQueryStringUseCase = CleanQueryStringUseCase()

    @Test
    fun `verify a regular query string`() {
        val given = "Regular string without extra spaces"
        val expected = "Regular string without extra spaces"

        assert(expected == verifyQueryStringUseCase(given))
    }

    @Test
    fun `clean a irregular query string with extra white spaces`() {
        val given = " Irregular string  with    extra spaces    "
        val expected = "Irregular string with extra spaces"

        assert(expected == verifyQueryStringUseCase(given))
    }
}
