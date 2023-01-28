package com.pp.students_organizer_backend.domain

case class MaterialEntity(
    id: Option[Int] = Option.empty,
    name: String,
    url: String
)
