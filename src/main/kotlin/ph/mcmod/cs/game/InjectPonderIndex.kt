package ph.mcmod.cs.game

import com.simibubi.create.AllBlocks
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper
import ph.mcmod.cs.MyRegistries

interface InjectPonderIndex {
    companion object {
        @JvmField
        val HELPER = PonderRegistrationHelper(MyRegistries.namespace)
        @JvmStatic
        fun register(helper: PonderRegistrationHelper) {
            HELPER.forComponents(AllBlocks.ITEM_DRAIN)
              .addStoryBoard("item_drain_barbecue", BarbecueScenes::drain)
        }
    }
   
}