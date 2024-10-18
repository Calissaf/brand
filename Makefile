run: compile
	docker compose up -d --build --force-recreate
compile:
	./gradlew clean build
psql:
	docker exec -it brand-db-1 psql