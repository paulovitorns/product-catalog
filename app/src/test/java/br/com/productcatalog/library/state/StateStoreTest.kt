package br.com.productcatalog.library.state

import br.com.productcatalog.screens.BaseUi
import org.junit.Test

class StateStoreTest {

    private val stateStore = StateStore()

    @Test
    fun `store a random state and retrieve it`() {
        val given = "It's a simple state"
        val expected = "It's a simple state"

        stateStore.save(BaseUi::class, given)

        assert(expected == stateStore.load(BaseUi::class))
    }

    @Test
    fun `store a random state and get a null after try to retrieve it twice`() {
        val given = "It's a simple state"

        stateStore.save(BaseUi::class, given)
        stateStore.load<String>(BaseUi::class)

        val retrievedTwice: String? = stateStore.load(BaseUi::class)

        assert(retrievedTwice == null)
    }

    @Test
    fun `overwrite an old stored state with a new one`() {
        val oldState = 1234
        stateStore.save(BaseUi::class, oldState)

        val newState = 4321
        stateStore.save(BaseUi::class, newState)

        val retrievedState: Int? = stateStore.load(BaseUi::class)

        assert(oldState != retrievedState)
        assert(newState == retrievedState)
    }

    @Test
    fun `get a null after try to retrieve a state without having previously saved`() {
        val retrievedState: Any? = stateStore.load(BaseUi::class)

        assert(retrievedState == null)
    }
}
