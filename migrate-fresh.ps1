# migrate-fresh.ps1
# Resets the inventory database and performs a fresh compilation

powershell -ExecutionPolicy Bypass -File db.ps1 fresh
