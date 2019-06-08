package br.com.productcatalog.library.injection.scope

import javax.inject.Scope

/**
 * A scope that indicates that the object returned by a binding keeps references as long as Fragment exists.
 *
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class FragmentScope
