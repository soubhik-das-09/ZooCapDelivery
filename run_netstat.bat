@echo off
netstat -ano | findstr :8080 > netstat_out.txt
