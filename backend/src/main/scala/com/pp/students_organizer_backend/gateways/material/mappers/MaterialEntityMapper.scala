package com.pp.students_organizer_backend.gateways.material.mappers

import com.pp.students_organizer_backend.domain.MaterialEntity
import com.pp.students_organizer_backend.domain.common.ValidationError
import com.pp.students_organizer_backend.routes.material.models.request.InsertMaterialRequest

object MaterialEntityMapper {
  def map(
      insertMaterialRequest: InsertMaterialRequest
  ): Either[ValidationError, MaterialEntity] =
    MaterialEntity.create(
      name = insertMaterialRequest.name,
      url = insertMaterialRequest.url
    )
}
