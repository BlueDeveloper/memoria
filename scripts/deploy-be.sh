#!/bin/bash
# Memoria BE 배포 스크립트
set -e

SSH_KEY="/c/BLUE/Project/blue/OracleCloud/instance/ssh-key-2026-01-23.key"
HOST="opc@152.69.235.170"
REMOTE_DIR="/opt/memoria-be"

echo "=== Memoria BE Deploy ==="

# 1. Build
echo "[1/4] Building..."
cd "$(dirname "$0")/../memoria-be"
./gradlew clean build -x test --no-daemon
echo "  Build OK"

# 2. Upload
echo "[2/4] Uploading JAR..."
scp -i $SSH_KEY build/libs/memoria-0.0.1-SNAPSHOT.jar $HOST:$REMOTE_DIR/app.jar
echo "  Upload OK"

# 3. Restart
echo "[3/4] Restarting service..."
ssh -i $SSH_KEY $HOST "sudo systemctl restart memoria-be"
echo "  Restart OK"

# 4. Health check
echo "[4/4] Health check (30s wait)..."
sleep 30
STATUS=$(ssh -i $SSH_KEY $HOST 'curl -s http://localhost:8084/v3/api-docs -o /dev/null -w "%{http_code}"')
if [ "$STATUS" = "200" ]; then
    echo "  Health check PASSED (HTTP $STATUS)"
    echo "=== Deploy SUCCESS ==="
else
    echo "  Health check FAILED (HTTP $STATUS)"
    ssh -i $SSH_KEY $HOST "sudo journalctl -u memoria-be --no-pager -n 20"
    exit 1
fi
