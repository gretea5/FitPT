#!/bin/bash
set -e

echo "[1/5] Python venv 생성 및 활성화"
python3 -m venv .venv
source .venv/bin/activate

echo "[2/5] pip requirements 설치"
pip install -r requirements.txt

echo "[3/5] Ansible 컬렉션 설치"
ansible-galaxy collection install -r requirements.yml

echo "[4/5] AWS SSM Session Manager Plugin 설치 여부 확인"
if ! command -v session-manager-plugin >/dev/null 2>&1; then
  echo "[SSM] session-manager-plugin 미설치 상태 → 설치 진행"
  curl -s "https://s3.amazonaws.com/session-manager-downloads/plugin/latest/ubuntu_64bit/session-manager-plugin.deb" -o ssm.deb
  sudo dpkg -i ssm.deb && rm ssm.deb
else
  echo "[SSM] session-manager-plugin 이미 설치됨"
fi

echo "[5/5] 완료 Ansible 및 SSM 연결 준비 완료!"
