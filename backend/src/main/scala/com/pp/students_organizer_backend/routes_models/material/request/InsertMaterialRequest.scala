package com.pp.students_organizer_backend.routes_models.material.request

import java.util.UUID

case class InsertMaterialRequest(
    name: String,
    url: String,
    assignmentId: String
)
