# db.ps1
# PowerShell Wrapper for the Java Database CLI Utility

$cmd = $args[0]
if (-not $cmd) {
    Write-Host "====================================================" -ForegroundColor Cyan
    Write-Host "       JAVA INVENTORY - DATABASE CLI UTILITY       " -ForegroundColor Cyan
    Write-Host "====================================================" -ForegroundColor Cyan
    Write-Host "Usage:"
    Write-Host "  powershell -ExecutionPolicy Bypass -File db.ps1 [command]"
    Write-Host ""
    Write-Host "Available commands:"
    Write-Host "  migrate   - Create database tables if they do not exist"
    Write-Host "  fresh     - Delete database and recreate empty tables (fresh start)"
    Write-Host "  seed      - Seed database with Toko Sembako sample dataset"
    Write-Host "  clear     - Wipe transaction and product records (keeps user accounts)"
    Write-Host "====================================================" -ForegroundColor Cyan
    exit 1
}

# Check and compile the CLI class if needed
Write-Host "Verifying binaries..." -ForegroundColor DarkGray
powershell -ExecutionPolicy Bypass -File build.ps1
if ($LASTEXITCODE -ne 0) {
    Write-Host "Failed to compile the project. Cannot run database command." -ForegroundColor Red
    exit $LASTEXITCODE
}

# Execute DatabaseCli with arguments
Write-Host "Executing database command '$cmd'..." -ForegroundColor Green
java -cp "bin;lib/*" com.inventory.database.DatabaseCli $cmd
