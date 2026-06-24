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

    # Package into an executable JAR
    Write-Host "Packaging into executable JAR..." -ForegroundColor Cyan

    # Resolve jar tool path dynamically
    $jarTool = "jar"
    if (Test-Path "$env:JAVA_HOME/bin/jar.exe") {
        $jarTool = "$env:JAVA_HOME/bin/jar.exe"
    } else {
        $foundJar = $null
        $searchPaths = @("C:\Program Files\Java\", "C:\Program Files (x86)\Java\")
        foreach ($searchPath in $searchPaths) {
            if (Test-Path $searchPath) {
                $jdkDirs = Get-ChildItem -Path $searchPath -Filter "jdk*" -ErrorAction SilentlyContinue | Sort-Object Name -Descending
                foreach ($dir in $jdkDirs) {
                    $testPath = "$($dir.FullName)/bin/jar.exe"
                    if (Test-Path $testPath) {
                        $foundJar = $testPath
                        break
                    }
                }
            }
            if ($foundJar) { break }
        }
        if ($foundJar) {
            $jarTool = $foundJar
        }
    }
    Write-Host "Using jar tool: $jarTool" -ForegroundColor DarkGray

    $stagingDir = "build_staging"
    if (Test-Path $stagingDir) {
        Remove-Item -Path $stagingDir -Recurse -Force
    }
    New-Item -ItemType Directory -Force -Path $stagingDir | Out-Null

    # Copy compiled classes from bin
    Copy-Item -Path "bin/*" -Destination $stagingDir -Recurse -Container -Force

    # Unpack dependencies from lib/
    $jars = Get-ChildItem -Path "lib" -Filter "*.jar"
    foreach ($jar in $jars) {
        Write-Host "Unpacking dependency: $($jar.Name)" -ForegroundColor DarkGray
        Push-Location $stagingDir
        & $jarTool -xf "../lib/$($jar.Name)"
        Pop-Location
    }

    # Clean security signature files inside META-INF to prevent SecurityException at runtime
    if (Test-Path "$stagingDir/META-INF") {
        Get-ChildItem -Path "$stagingDir/META-INF" -Include *.SF, *.DSA, *.RSA -Recurse | Remove-Item -Force
    }

    # Write custom manifest
    $manifestPath = "manifest.txt"
    $manifestContent = @"
Manifest-Version: 1.0
Main-Class: com.inventory.main.Main
Multi-Release: true

"@
    [System.IO.File]::WriteAllText($manifestPath, $manifestContent)

    # Package staging directory to JAR
    $jarName = "Java-Inventory.jar"
    if (Test-Path $jarName) {
        Remove-Item -Path $jarName -Force
    }

    Write-Host "Creating fat JAR: $jarName..." -ForegroundColor Green
    & $jarTool -cfm $jarName $manifestPath -C $stagingDir .

    # Clean up staging and manifest
    Remove-Item -Path $stagingDir -Recurse -Force -ErrorAction SilentlyContinue
    Remove-Item -Path $manifestPath -Force -ErrorAction SilentlyContinue

    Write-Host "Successfully generated executable JAR: $jarName" -ForegroundColor Green
} else {
    Write-Host "Compilation failed with exit code $LASTEXITCODE."
    exit $LASTEXITCODE
}
