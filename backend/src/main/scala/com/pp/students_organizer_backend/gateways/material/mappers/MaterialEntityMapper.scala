package com.pp.students_organizer_backend.gateways.material.mappers

import com.pp.students_organizer_backend.domain.MaterialEntity
import com.pp.students_organizer_backend.routes.material.models.request.InsertMaterialRequest

object MaterialEntityMapper {
  def map(insertMaterialRequest: InsertMaterialRequest): MaterialEntity =
    MaterialEntity(
      id = Option.empty, 
      name = insertMaterialRequest.name, 
      url = insertMaterialRequest.url
    )
}
