package ntnu.idatt2105.section.dto

import ntnu.idatt2105.util.SectionType
import java.util.*

data class SectionChildrenDto(var id: UUID = UUID.randomUUID(),
                              var name: String = "",
                              var capacity: Int = 0,
                              var type: String = SectionType.ROOM,
                             )