package com.pp.students_organizer_backend.utils

import com.pp.students_organizer_backend.domain.{AssignmentTypeId, AssignmentTypeName, MaterialId, MaterialName, MaterialUrl}
import skunk.Codec
import skunk.codec.all.varchar
import skunk.codec.uuid.uuid

object DatabaseCodes:
  object AssignmentType:
    val assignmentTypeId: Codec[AssignmentTypeId] = uuid.imap[AssignmentTypeId](AssignmentTypeId)(_.value)
    val assignmentTypeName: Codec[AssignmentTypeName] = varchar.imap[AssignmentTypeName](AssignmentTypeName)(_.value)

  // Material Codes
  val materialId: Codec[MaterialId] = uuid.imap[MaterialId](MaterialId)(_.value)
  val materialName: Codec[MaterialName] = varchar.imap[MaterialName](MaterialName)(_.value)
  val materialUrl: Codec[MaterialUrl] = varchar.imap[MaterialUrl](MaterialUrl)(_.value)


