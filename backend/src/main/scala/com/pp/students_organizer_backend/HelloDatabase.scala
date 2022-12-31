package com.pp.students_organizer_backend

import cats.effect.*
import com.pp.students_organizer_backend.services.database.AssignmentTypeService
import com.pp.students_organizer_backend.services.database.di.DatabaseDI
import com.pp.students_organizer_backend.services.database.models.AssignmentTypeModel
import com.pp.students_organizer_backend.use_cases.assignment_type.GetAssignmentTypes
import com.pp.students_organizer_backend.use_cases.di.AssignmentTypeDI
import natchez.Trace.Implicits.noop
import skunk.*
import skunk.codec.all.*
import skunk.implicits.*

object HelloDatabase extends IOApp:
  def run(args: List[String]): IO[ExitCode] =
    val getAssignmentTypes = AssignmentTypeDI.getAssignmentTypes(
      databaseResource = DatabaseDI.sessionResource,
    )

    for {
      types <- getAssignmentTypes()
      _ <- IO.print(types)
    } yield ExitCode.Success
