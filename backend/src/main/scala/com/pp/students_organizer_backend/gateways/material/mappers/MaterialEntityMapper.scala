package com.pp.students_organizer_backend.gateways.material.mappers

import com.pp.students_organizer_backend.domain.MaterialEntity
import com.pp.students_organizer_backend.domain.errors.ValidationError
import com.pp.students_organizer_backend.routes.material.models.request.InsertMaterialRequest

trait MaterialEntityMapper:
  def map(
      insertMaterialRequest: InsertMaterialRequest
  ): Either[ValidationError, MaterialEntity]

object MaterialEntityMapper:
  given materialEntityMapper: MaterialEntityMapper with {
    override def map(
        insertMaterialRequest: InsertMaterialRequest
    ): Either[ValidationError, MaterialEntity] =
      MaterialEntity.create(
        name = insertMaterialRequest.name,
        url = insertMaterialRequest.url
      )
  }
