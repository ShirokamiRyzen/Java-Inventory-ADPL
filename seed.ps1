# seed.ps1
# Runs the database seeder to populate the SQLite database with Sembako test data

Write-Host "Compiling seeder and other source files..."
powershell -ExecutionPolicy Bypass -File build.ps1

if ($LASTEXITCODE -ne 0) {
    Write-Host "Failed to compile the project. Cannot seed database."
    exit $LASTEXITCODE
}

Write-Host "Running Database Seeder..."
# Execute the Java seeder class
java -cp "bin;lib/*" com.inventory.database.DatabaseSeeder

if ($LASTEXITCODE -eq 0) {
    Write-Host "Database successfully seeded with Toko Sembako test data!"
} else {
    Write-Host "Failed to seed database."
    exit $LASTEXITCODE
}
