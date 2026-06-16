# Check if bin directory has compiled files, if not run compilation
if (-not (Test-Path "bin/com/inventory/main/Main.class")) {
    Write-Host "Binaries not found. Compiling first..."
    powershell -ExecutionPolicy Bypass -File build.ps1
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Failed to compile the project. Cannot run."
        exit $LASTEXITCODE
    }
}

Write-Host "Starting Inventory Application..."
# Run the java application with classpath set to bin and lib jars
java -cp "bin;lib/*" com.inventory.main.Main
