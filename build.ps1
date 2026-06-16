# Ensure bin directory exists
New-Item -ItemType Directory -Force -Path "bin"

# Get all Java source files and write to sources.txt cleanly without BOM
$files = Get-ChildItem -Path "src" -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }
[System.IO.File]::WriteAllLines("sources.txt", $files)

# Compile classes
Write-Host "Compiling source files..."
javac -d bin -cp "lib/*" "@sources.txt"

if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilation successful! Classes are stored in 'bin/'."
    # Clean up sources.txt
    Remove-Item -Path "sources.txt" -Force
} else {
    Write-Host "Compilation failed with exit code $LASTEXITCODE."
    exit $LASTEXITCODE
}
