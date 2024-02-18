package khome.core.koin

import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.koin.core.Koin
import org.koin.core.component.KoinComponent

@ObsoleteCoroutinesApi
internal interface KhomeComponent : KoinComponent {
    override fun getKoin(): Koin = checkNotNull(KhomeKoinContext.application) { "No KoinApplication found" }.koin
}

internal object KoinContainer : KhomeComponent
