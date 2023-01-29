package com.pp.students_organizer_backend.routes.material.models.response

import java.util.UUID

case class GetMaterialResponse(
    id: UUID,
    name: String,
    url: String
)
