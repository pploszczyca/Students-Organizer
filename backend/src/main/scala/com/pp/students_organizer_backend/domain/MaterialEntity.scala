package com.pp.students_organizer_backend.domain

import com.pp.students_organizer_backend.domain.errors.ValidationError
import com.pp.students_organizer_backend.utils.validators.ValidateEmptyString.*

import java.util.UUID

case class MaterialEntity(
    id: MaterialId,
    name: MaterialName,
    url: MaterialUrl
)

object MaterialEntity:
  def create(
      id: UUID,
      name: String,
      url: String
  ): Either[ValidationError, MaterialEntity] =
    for
      materialName <- MaterialName.create(name)
      materialUrl <- MaterialUrl.create(url)
    yield MaterialEntity(
      id = MaterialId(id),
      name = materialName,
      url = materialUrl
    )

  def create(
      name: String,
      url: String
  ): Either[ValidationError, MaterialEntity] =
    create(
      id = UUID.randomUUID(),
      name = name,
      url = url
    )

case class MaterialId(value: UUID)

case class MaterialName(value: String)
object MaterialName:
  def create(value: String): Either[ValidationError, MaterialName] =
    value
      .validateEmptyString("Material name can't be empty")
      .map(MaterialName.apply)

case class MaterialUrl(value: String)
object MaterialUrl:
  def create(value: String): Either[ValidationError, MaterialUrl] =
    value
      .validateEmptyString("Material url can't be empty")
      .map(MaterialUrl.apply)
