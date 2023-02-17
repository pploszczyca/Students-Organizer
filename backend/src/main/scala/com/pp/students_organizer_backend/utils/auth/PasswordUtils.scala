package com.pp.students_organizer_backend.utils.auth

import com.pp.students_organizer_backend.domain.{
  StudentEncodedPassword,
  StudentPassword
}

import java.util.Base64

trait PasswordUtils:
  def encode(password: StudentPassword): StudentEncodedPassword
  def decode(encodedPassword: StudentEncodedPassword): StudentPassword

object PasswordUtils:
  def make: PasswordUtils =
    new PasswordUtils:
      private val base64Encoder = Base64.getEncoder
      private val base64Decoder = Base64.getDecoder
      private val charsetName = "UTF-8"

      override def encode(
          password: StudentPassword
      ): StudentEncodedPassword =
        StudentEncodedPassword(
          value = new String(
            base64Encoder.encode(password.value.getBytes(charsetName)),
            charsetName
          )
        )

      override def decode(
          encodedPassword: StudentEncodedPassword
      ): StudentPassword =
        StudentPassword(
          value = new String(
            base64Decoder.decode(encodedPassword.value.getBytes(charsetName)),
            charsetName
          )
        )
