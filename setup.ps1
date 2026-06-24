# Create directories
New-Item -ItemType Directory -Force -Path "lib"
New-Item -ItemType Directory -Force -Path "src/com/inventory/main"
New-Item -ItemType Directory -Force -Path "src/com/inventory/database"
New-Item -ItemType Directory -Force -Path "src/com/inventory/model"
New-Item -ItemType Directory -Force -Path "src/com/inventory/dao"
New-Item -ItemType Directory -Force -Path "src/com/inventory/ui"
New-Item -ItemType Directory -Force -Path "src/com/inventory/ui/components"
New-Item -ItemType Directory -Force -Path "src/com/inventory/ui/theme"

# URLs for required libraries
$flatlafUrl = "https://repo1.maven.org/maven2/com/formdev/flatlaf/3.5.1/flatlaf-3.5.1.jar"
$mariadbUrl = "https://repo1.maven.org/maven2/org/mariadb/jdbc/mariadb-java-client/3.5.1/mariadb-java-client-3.5.1.jar"
$slf4jApiUrl = "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.12/slf4j-api-2.0.12.jar"
$slf4jSimpleUrl = "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.12/slf4j-simple-2.0.12.jar"

# Download helper function
function Download-File ($url, $dest) {
    if (Test-Path $dest) {
        Write-Host "$dest already exists, skipping."
    } else {
        Write-Host "Downloading $url to $dest..."
        Invoke-WebRequest -Uri $url -OutFile $dest -UserAgent "Mozilla/5.0"
    }
}

Download-File $flatlafUrl "lib/flatlaf-3.5.1.jar"
if (Test-Path "lib/sqlite-jdbc-3.45.2.0.jar") {
    Remove-Item -Path "lib/sqlite-jdbc-3.45.2.0.jar" -Force
}
Download-File $mariadbUrl "lib/mariadb-java-client-3.5.1.jar"
Download-File $slf4jApiUrl "lib/slf4j-api-2.0.12.jar"
Download-File $slf4jSimpleUrl "lib/slf4j-simple-2.0.12.jar"

Write-Host "All libraries downloaded and directories created successfully!"
