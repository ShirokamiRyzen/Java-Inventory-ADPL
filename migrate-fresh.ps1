# migrate-fresh.ps1
# Resets the inventory database and performs a fresh compilation

if (Test-Path "inventory.db") {
    Write-Host "Resetting database by removing 'inventory.db'..."
    # Force removal of the database file
    Remove-Item "inventory.db" -Force
    Write-Host "Database file deleted successfully."
} else {
    Write-Host "No database file 'inventory.db' exists yet."
}

# Recompile to ensure latest seeding scripts are active
Write-Host "Recompiling source files..."
powershell -ExecutionPolicy Bypass -File build.ps1

if ($LASTEXITCODE -eq 0) {
    Write-Host "Database migration:fresh completed! Run 'powershell -ExecutionPolicy Bypass -File run.ps1' to launch the fresh app."
} else {
    Write-Host "Failed to compile the project during migrate:fresh."
    exit $LASTEXITCODE
}
