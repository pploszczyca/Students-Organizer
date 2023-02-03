package com.pp.students_organizer_backend.routes_models.material.response

import java.util.UUID

case class GetMaterialResponse(
    id: UUID,
    name: String,
    url: String
)
