package com.pp.students_organizer_backend.gateways.material.mappers

import com.pp.students_organizer_backend.domain.errors.ValidationError
import com.pp.students_organizer_backend.domain.{AssignmentId, MaterialEntity}
import com.pp.students_organizer_backend.routes_models.material.request.InsertMaterialRequest

import java.util.UUID

trait MaterialEntityMapper:
  def map(
      insertMaterialRequest: InsertMaterialRequest
  ): Either[ValidationError, MaterialEntity]

object MaterialEntityMapper:
  given materialEntityMapper: MaterialEntityMapper with {
    override def map(
        insertMaterialRequest: InsertMaterialRequest
    ): Either[ValidationError, MaterialEntity] =
      for
        assignmentId <- AssignmentId.create(insertMaterialRequest.assignmentId)
        material <- MaterialEntity.create(
          name = insertMaterialRequest.name,
          url = insertMaterialRequest.url,
          assignmentId = assignmentId
        )
      yield material
  }
