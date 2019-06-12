package br.com.productcatalog.screens

import br.com.productcatalog.domain.CleanQueryStringUseCase
import br.com.productcatalog.library.state.StateStore
import br.com.productcatalog.screens.home.HomePresenter
import br.com.productcatalog.screens.home.HomeUi
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class HomePresenterTest {

    private val verifyQueryStringUseCase = mock<CleanQueryStringUseCase>()
    private val stateStore = mock<StateStore>()
    private val dashboardPresenter = HomePresenter(stateStore, verifyQueryStringUseCase)
    private val dashboardUi = mock<HomeUi>()

    init {
        dashboardPresenter.setUi(dashboardUi)
    }

    @Test
    fun `show search screen passing a regular query string`() {
        val query = "Google Pixel 3A"

        given(verifyQueryStringUseCase(query)).willReturn(query)
        val expected = verifyQueryStringUseCase(query)

        dashboardPresenter.onSendSearch(query)

        verify(dashboardUi).openSearchScreen(expected)
    }

    @Test
    fun `show search screen passing a irregular query string`() {
        val query = "  Google   Pixel    3A "

        given(verifyQueryStringUseCase(query)).willReturn("Google Pixel 3A")
        val expected = verifyQueryStringUseCase(query)

        dashboardPresenter.onSendSearch(query)

        verify(dashboardUi).openSearchScreen(expected)
    }
}
