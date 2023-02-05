package com.pp.students_organizer_backend.utils

import com.pp.students_organizer_backend.domain.*
import skunk.Codec
import skunk.codec.all.{bool, timestamp, varchar}
import skunk.codec.uuid.uuid

object DatabaseCodec:
  object AssignmentType:
    val assignmentTypeId: Codec[AssignmentTypeId] =
      uuid.imap[AssignmentTypeId](AssignmentTypeId.apply)(_.value)
    val assignmentTypeName: Codec[AssignmentTypeName] =
      varchar.imap[AssignmentTypeName](AssignmentTypeName.apply)(_.value)

  object Material:
    val materialId: Codec[MaterialId] =
      uuid.imap[MaterialId](MaterialId.apply)(_.value)
    val materialName: Codec[MaterialName] =
      varchar.imap[MaterialName](MaterialName.apply)(_.value)
    val materialUrl: Codec[MaterialUrl] =
      varchar.imap[MaterialUrl](MaterialUrl.apply)(_.value)

  object TaskEntity:
    val taskId: Codec[TaskId] = uuid.imap[TaskId](TaskId.apply)(_.value)
    val taskName: Codec[TaskName] =
      varchar.imap[TaskName](TaskName.apply)(_.value)
    val taskIsDone: Codec[TaskIsDone] =
      bool.imap[TaskIsDone](TaskIsDone.apply)(_.value)

  object AssignmentEntity:
    val assignmentId: Codec[AssignmentId] =
      uuid.imap[AssignmentId](AssignmentId.apply)(_.value)
    val assignmentName: Codec[AssignmentName] =
      varchar.imap[AssignmentName](AssignmentName.apply)(_.value)
    val assignmentDescription: Codec[AssignmentDescription] =
      varchar.imap[AssignmentDescription](AssignmentDescription.apply)(_.value)
    val assignmentStatus: Codec[AssignmentStatus] =
      varchar.imap[AssignmentStatus](AssignmentStatus.valueOf)(_.toString)
    val assignmentEndDate: Codec[AssignmentEndDate] =
      timestamp.imap[AssignmentEndDate](
        AssignmentEndDate.apply
      )(_.value)
