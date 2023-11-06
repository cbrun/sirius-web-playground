

```
docker run -p 5433:5432 --rm --name playground-postgres -e POSTGRES_USER=dbuser -e POSTGRES_PASSWORD=dbpwd -e POSTGRES_DB=playground-db -d postgres:12
```