package com.pp.students_organizer_backend.gateways.material.mappers

import com.pp.students_organizer_backend.domain.MaterialEntity
import com.pp.students_organizer_backend.routes.material.models.response.GetMaterialResponse

object GetMaterialResponseMapper {
  def map(material: MaterialEntity): GetMaterialResponse =
    GetMaterialResponse(
      id = material.id.value,
      name = material.name.value,
      url = material.url.value,
    )
}