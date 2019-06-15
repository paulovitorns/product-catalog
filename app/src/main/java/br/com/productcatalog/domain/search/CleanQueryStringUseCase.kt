package br.com.productcatalog.domain.search

import br.com.productcatalog.library.extension.removeExtrasWhiteSpaces
import dagger.Reusable
import javax.inject.Inject

@Reusable
class CleanQueryStringUseCase @Inject constructor() {
    operator fun invoke(query: String) = query.removeExtrasWhiteSpaces()
}
