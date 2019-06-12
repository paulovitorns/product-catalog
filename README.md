# Product catalog

This is a simple list and search product app using the public 
[API](https://developers.mercadolibre.com.ar/es_ar/items-y-busquedas) from Mercado Libre.

## Mercado Libre API

Since we'll use only public endpoints we don't have to create a project or authenticate our session on this sample.  

## Project architecture

The architecture chosen to this project was the MVI.

MVI stands for Model-View-Intent. MVI is one of the newest architecture patterns for Android, inspired by the 
unidirectional and cyclical nature of the Cycle.js framework.

MVI works in a very different way compared to its distant relatives, MVC, MVP or MVVM. The role of each MVI 
components is as follows:

- Model represents a viewState. Models in MVI should be immutable to ensure a unidirectional data flow between them and 
the other layers in your architecture.
- Like in MVP, Interfaces in MVI represent Views, which are then implemented in one or more Activities or Fragments.
- Intent represents an intention or a desire to perform an action, either by the user or the app itself. For every 
action, a View receives an Intent. The Presenter observes the Intent, and Models translate it into a new viewState.

> [MVI](https://www.raywenderlich.com/817602-mvi-architecture-for-android-tutorial-getting-started) Reference.

## Versioning

The versioning scheme follows `major.minor.commit_count`. Major and minor numbers are 
increased manually. Commit count is extracted from the number of commits present in 
the `master` branch. Run this task to check the latest version `./gradlew version`.

## Dependency Injection

[Dagger](https://google.github.io/dagger/) is the tool who help us here. It is a fully static, compile-time 
dependency injection framework for both Java and Android. 
Take at look [here](https://google.github.io/dagger/android.html) if you want to learn more. 

## Code Style

This project is following the Kotlin code style guideline. 
To read more about it just follow it on [Kotlin code style guide](https://android.github.io/kotlin-guides/style.html).

## Linting

Run Kotlin lint:
`./gradlew ktlint`

Run Kotlin lint and apply automatic fixes:
`./gradlew ktlintFormat`

Using [RxLint](https://bitbucket.org/littlerobots/rxlint/src/default/) to make sure we'll our subscribers will 
handle the `onError()` callback or it was added to some `CompositeDisposable` to avoid memory leaks
and crash by not dispose the subscription correctly.

## Testing

Run unit tests: 
`./gradlew testDebugUnitTest`
