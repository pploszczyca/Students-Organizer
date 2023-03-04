# Students-Organizer

## Description
Project focused on preparing simple API for Student's Organizer.

## Technologies
* [Scala 3](https://www.scala-lang.org/)
* [Cats](https://typelevel.org/cats/)
* [Http4s](https://typelevel.org/cats/)
* [Skunk](https://tpolecat.github.io/skunk/)
* [jwt-scala](https://github.com/jwt-scala/jwt-scala)
* [ScalaTest](https://www.scalatest.org/)
* [Mockito for ScalaTest](https://www.scalatest.org/plus/mockito)
* [Redis](https://redis.io/)
* [PostgreSQL](https://www.postgresql.org/)

## How to run
### Databases
Make sure that PostgreSQL and Redis is running on ports `5432` and `6379`.

You can run PostgreSQL for example via Docker:
```
$ docker run --name some-postgres -e POSTGRES_PASSWORD=password -e POSTGRES_USER=postgres -e POSTGRES_DB=postgres -p 5432:5432 -d postgres
```

You can run Redis for example via Docker:
```
$ docker run --name some-redis -p 6379:6379 -d redis
```

#### Schema and mock data
Schema and mock database date are under [resources](backend/src/main/resources) folder. 

### Backend
This project is using [sbt](https://www.scala-sbt.org/).

Go to `backend` folder and run:
```
$ sbt compile
$ sbt run
```
The backend should be start running on `localhost:8080`.

## API usage
Examples of how to use API are in [resources/http_requests](backend/src/main/resources/http_requests/) folder

## Features
- [X] Login/Register via JWT
- [X] Managing assignments (get/create/update/delete)
- [X] Managing tasks and materials for assignment (get/create/delete)
- [X] Error handling
- [ ] Managing Subjects, Terms