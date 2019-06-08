package br.com.productcatalog.library.injection.scope

import javax.inject.Scope

/**
 * A scope that indicates that the object returned by a binding keeps references as long as Activity exists.
 *
 * <p>{@code @ActivityScope} is useful when you want to keeping single instance as long that the scope exists.
 * In this case, the activity can share any single instances with fragments hosted in this Activity.
 *
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ActivityScope
