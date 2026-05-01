#!/bin/bash
# Memoria 로컬 개발 환경 실행 스크립트
# 사용법: bash scripts/run-local.sh

set -e

echo "=== Memoria Local Dev ==="

# 환경변수
export DB_PASSWORD="Brp2026Cal!#"
export JWT_SECRET="bWVtb3JpYS1kZXYtc2VjcmV0LWtleS1tdXN0LWJlLWF0LWxlYXN0LTI1Ni1iaXRzLWxvbmctZm9yLWhzMjU2LWFsZ29yaXRobQ=="

# BE 실행
echo "[BE] Starting on port 8080..."
cd "$(dirname "$0")/../memoria-be"
./gradlew bootRun --no-daemon &
BE_PID=$!

# FE 실행
echo "[FE] Starting on port 3001..."
cd "$(dirname "$0")/../memoria-fe"
NEXT_PUBLIC_API_URL=http://localhost:8080 npx next dev -p 3001 &
FE_PID=$!

echo ""
echo "BE: http://localhost:8080 (PID: $BE_PID)"
echo "FE: http://localhost:3001 (PID: $FE_PID)"
echo "Swagger: http://localhost:8080/swagger-ui.html"
echo ""
echo "Press Ctrl+C to stop both"

trap "kill $BE_PID $FE_PID 2>/dev/null; exit" INT TERM
wait
