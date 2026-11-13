<#
 GrabNGo - one-time developer setup for Windows 10/11.
 Installs Git, GitHub CLI and Android Studio (only if missing) using winget,
 then optionally clones the repo and opens it in Android Studio.

 Run (from the repo folder or anywhere):
     powershell -ExecutionPolicy Bypass -File .\setup.ps1
 Optional clone + open:
     powershell -ExecutionPolicy Bypass -File .\setup.ps1 -RepoUrl https://github.com/<owner>/<repo>.git
#>
param(
    [string]$RepoUrl = "",
    [string]$TargetFolder = "$HOME\GrabNGo"
)

$ErrorActionPreference = "Stop"

function Write-Step($msg) { Write-Host "`n==> $msg" -ForegroundColor Cyan }
function Refresh-Path {
    $machine = [Environment]::GetEnvironmentVariable("Path", "Machine")
    $user    = [Environment]::GetEnvironmentVariable("Path", "User")
    $env:Path = "$machine;$user"
}
function Has-Command($name) { return [bool](Get-Command $name -ErrorAction SilentlyContinue) }
function Winget-Install($id, $label) {
    Write-Host "Installing $label ($id)..."
    winget install --id $id --exact --silent `
        --accept-package-agreements --accept-source-agreements
    if ($LASTEXITCODE -ne 0) {
        Write-Warning "winget reported exit code $LASTEXITCODE for $label. It may already be installed or need a restart."
    }
}
function Find-AndroidStudio {
    $candidates = @(
        "$env:ProgramFiles\Android\Android Studio\bin\studio64.exe",
        "$env:LOCALAPPDATA\Programs\Android Studio\bin\studio64.exe"
    )
    foreach ($c in $candidates) { if (Test-Path $c) { return $c } }
    return $null
}

# 1. winget
Write-Step "Checking winget"
if (-not (Has-Command "winget")) {
    Write-Error "winget was not found. Update 'App Installer' from the Microsoft Store, or install Android Studio manually from https://developer.android.com/studio"
}
Write-Host "winget OK"

# 2. Git
Write-Step "Checking Git"
if (Has-Command "git") { Write-Host "Git already installed: $(git --version)" }
else { Winget-Install "Git.Git" "Git"; Refresh-Path }

# 3. GitHub CLI (used by scripts/setup-github.sh)
Write-Step "Checking GitHub CLI"
if (Has-Command "gh") { Write-Host "GitHub CLI already installed" }
else { Winget-Install "GitHub.cli" "GitHub CLI"; Refresh-Path }

# 4. Android Studio (includes its own JDK and the SDK manager)
Write-Step "Checking Android Studio"
$studio = Find-AndroidStudio
if ($studio) { Write-Host "Android Studio already installed at: $studio" }
else {
    Winget-Install "Google.AndroidStudio" "Android Studio"
    Refresh-Path
    $studio = Find-AndroidStudio
}

# 5. Emulator hint: virtualization must be enabled in BIOS/UEFI
Write-Step "Checking virtualization (needed for the emulator)"
try {
    $cpu = Get-CimInstance Win32_Processor | Select-Object -First 1
    if ($cpu.VirtualizationFirmwareEnabled) { Write-Host "Virtualization is enabled" }
    else { Write-Warning "Virtualization looks disabled. Enable Intel VT-x / AMD-V in BIOS, or test on a physical phone." }
} catch { Write-Host "Could not check virtualization (skipping)." }

# 6. Optional: clone the repo
$projectPath = $null
if ($RepoUrl -ne "") {
    Write-Step "Cloning $RepoUrl"
    if (Test-Path $TargetFolder) { Write-Host "$TargetFolder already exists, skipping clone" }
    else { git clone $RepoUrl $TargetFolder }
    $projectPath = $TargetFolder
}

# 7. Optional: open the project
if ($studio -and $projectPath) {
    Write-Step "Opening the project in Android Studio"
    Start-Process -FilePath $studio -ArgumentList "`"$projectPath`""
}

Write-Host "`nDone." -ForegroundColor Green
Write-Host @"

Next steps:
  1. Open Android Studio once and finish the setup wizard (it downloads the Android SDK).
  2. Open the GrabNGo project and let Gradle sync. Accept any 'Install missing SDK' prompts.
  3. Device Manager -> create an emulator (API 34 recommended), then press Run.
  4. Optional: run 'gh auth login' so you can use scripts/setup-github.sh.
"@
