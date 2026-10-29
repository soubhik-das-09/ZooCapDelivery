@echo off
mvnw.cmd spring-boot:run -Dmaven.test.skip=true > dev_backend_log.txt 2>&1
