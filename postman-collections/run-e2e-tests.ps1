# =============================================================================
# E2E Tests Automation Script for E-commerce Microservices
# =============================================================================
# This script runs all 5 Postman collections for comprehensive E2E testing
# Make sure Newman (Postman CLI) is installed: npm install -g newman
# =============================================================================

param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$ReportsDir = "test-reports",
    [switch]$GenerateReports = $true,
    [switch]$StopOnFailure = $false
)

# Colors for output
$Green = "`e[32m"
$Red = "`e[31m"
$Yellow = "`e[33m"
$Blue = "`e[34m"
$Reset = "`e[0m"

# Test collections configuration
$collections = @(
    @{
        Name = "User Registration and Login Flow"
        File = "1-user-registration-login-flow.json"
        Description = "Tests user creation, authentication, and profile management"
    },
    @{
        Name = "Product Catalog and Category Management"
        File = "2-product-catalog-management.json"
        Description = "Tests product and category CRUD operations and catalog navigation"
    },
    @{
        Name = "Shopping Cart and Order Process"
        File = "3-shopping-cart-order-process.json"
        Description = "Tests cart management and order creation workflow"
    },
    @{
        Name = "Payment and Shipping Integration"
        File = "4-payment-shipping-integration.json"
        Description = "Tests payment processing and shipping management"
    },
    @{
        Name = "User Favorites and Recommendations"
        File = "5-user-favorites-recommendations.json"
        Description = "Tests favorites system and recommendation features"
    }
)

function Write-Header {
    param([string]$Message)
    Write-Host ""
    Write-Host "===============================================================================" -ForegroundColor Blue
    Write-Host " $Message" -ForegroundColor Blue
    Write-Host "===============================================================================" -ForegroundColor Blue
    Write-Host ""
}

function Write-Success {
    param([string]$Message)
    Write-Host "✅ $Message" -ForegroundColor Green
}

function Write-Error {
    param([string]$Message)
    Write-Host "❌ $Message" -ForegroundColor Red
}

function Write-Warning {
    param([string]$Message)
    Write-Host "⚠️  $Message" -ForegroundColor Yellow
}

function Write-Info {
    param([string]$Message)
    Write-Host "ℹ️  $Message" -ForegroundColor Cyan
}

function Test-Prerequisites {
    Write-Header "Checking Prerequisites"
    
    # Check if Newman is installed
    try {
        $newmanVersion = newman --version 2>$null
        if ($newmanVersion) {
            Write-Success "Newman is installed (version: $newmanVersion)"
        } else {
            throw "Newman not found"
        }
    } catch {
        Write-Error "Newman is not installed. Please install it using: npm install -g newman"
        exit 1
    }
    
    # Check if API Gateway is running
    try {
        Write-Info "Checking API Gateway health at $BaseUrl..."
        $response = Invoke-RestMethod -Uri "$BaseUrl/actuator/health" -Method Get -TimeoutSec 10
        Write-Success "API Gateway is healthy and running"
    } catch {
        Write-Error "API Gateway is not accessible at $BaseUrl"
        Write-Warning "Please ensure the e-commerce microservices are running"
        exit 1
    }
    
    # Create reports directory if generating reports
    if ($GenerateReports) {
        if (!(Test-Path $ReportsDir)) {
            New-Item -ItemType Directory -Path $ReportsDir -Force | Out-Null
            Write-Success "Created reports directory: $ReportsDir"
        }
    }
}

function Run-Collection {
    param(
        [string]$CollectionName,
        [string]$CollectionFile,
        [string]$Description,
        [int]$CollectionNumber
    )
    
    Write-Header "Running Test $CollectionNumber/5: $CollectionName"
    Write-Info $Description
    
    $collectionPath = Join-Path $PSScriptRoot $CollectionFile
    
    if (!(Test-Path $collectionPath)) {
        Write-Error "Collection file not found: $collectionPath"
        return $false
    }
      # Prepare Newman command
    $newmanArgs = @(
        "run", $collectionPath,
        "--env-var", "base_url=$BaseUrl",
        "--global-var", "API_GATEWAY_URL=$BaseUrl",
        "--color", "on",
        "--reporter", "cli"
    )
    
    # Add reports if enabled
    if ($GenerateReports) {
        $reportName = "test-$CollectionNumber-$(($CollectionName -replace '[^a-zA-Z0-9]', '-').ToLower())"
        $htmlReport = Join-Path $ReportsDir "$reportName.html"
        $jsonReport = Join-Path $ReportsDir "$reportName.json"
        
        $newmanArgs += @(
            "--reporter", "cli,html,json",
            "--reporter-html-export", $htmlReport,
            "--reporter-json-export", $jsonReport
        )
    }
    
    # Run the collection
    Write-Info "Executing Newman command..."
    $startTime = Get-Date
    
    try {
        & newman @newmanArgs
        $exitCode = $LASTEXITCODE
        $endTime = Get-Date
        $duration = $endTime - $startTime
        
        if ($exitCode -eq 0) {
            Write-Success "✅ Test completed successfully in $($duration.TotalSeconds.ToString('F2')) seconds"
            if ($GenerateReports) {
                Write-Info "Reports generated in $ReportsDir directory"
            }
            return $true
        } else {
            Write-Error "❌ Test failed with exit code: $exitCode"
            return $false
        }
    } catch {
        Write-Error "Failed to execute Newman: $($_.Exception.Message)"
        return $false
    }
}

# =============================================================================
# MAIN EXECUTION
# =============================================================================

Write-Header "E-commerce Microservices E2E Test Suite"
Write-Info "Base URL: $BaseUrl"
Write-Info "Generate Reports: $GenerateReports"
Write-Info "Stop on Failure: $StopOnFailure"

# Check prerequisites
Test-Prerequisites

# Initialize results tracking
$results = @()
$totalTests = $collections.Count
$passedTests = 0
$failedTests = 0

# Execute each collection
for ($i = 0; $i -lt $collections.Count; $i++) {
    $collection = $collections[$i]
    $success = Run-Collection -CollectionName $collection.Name -CollectionFile $collection.File -Description $collection.Description -CollectionNumber ($i + 1)
    
    $results += @{
        Name = $collection.Name
        Success = $success
        Number = $i + 1
    }
    
    if ($success) {
        $passedTests++
    } else {
        $failedTests++
        if ($StopOnFailure) {
            Write-Warning "Stopping execution due to test failure (StopOnFailure is enabled)"
            break
        }
    }
    
    # Add delay between tests to avoid overwhelming the API
    if ($i -lt $collections.Count - 1) {
        Write-Info "Waiting 3 seconds before next test..."
        Start-Sleep -Seconds 3
    }
}

# =============================================================================
# FINAL RESULTS SUMMARY
# =============================================================================

Write-Header "Test Execution Summary"

Write-Host "📊 " -NoNewline
Write-Host "OVERALL RESULTS:" -ForegroundColor Blue
Write-Host "   Total Tests: $totalTests"
Write-Host "   Passed: " -NoNewline
Write-Host "$passedTests" -ForegroundColor Green
Write-Host "   Failed: " -NoNewline
Write-Host "$failedTests" -ForegroundColor Red
Write-Host "   Success Rate: " -NoNewline

$successRate = if ($totalTests -gt 0) { ($passedTests / $totalTests) * 100 } else { 0 }
$color = if ($successRate -eq 100) { "Green" } elseif ($successRate -ge 75) { "Yellow" } else { "Red" }
Write-Host "$($successRate.ToString('F1'))%" -ForegroundColor $color

Write-Host ""
Write-Host "📝 " -NoNewline
Write-Host "DETAILED RESULTS:" -ForegroundColor Blue

foreach ($result in $results) {
    $status = if ($result.Success) { "✅ PASSED" } else { "❌ FAILED" }
    $statusColor = if ($result.Success) { "Green" } else { "Red" }
    Write-Host "   Test $($result.Number): " -NoNewline
    Write-Host $status -ForegroundColor $statusColor -NoNewline
    Write-Host " - $($result.Name)"
}

if ($GenerateReports) {
    Write-Host ""
    Write-Host "📁 " -NoNewline
    Write-Host "REPORTS:" -ForegroundColor Blue
    Write-Host "   Reports directory: $ReportsDir"
    Write-Host "   HTML and JSON reports generated for each test"
}

Write-Host ""
if ($failedTests -eq 0) {
    Write-Success "🎉 ALL TESTS PASSED! E-commerce microservices are working correctly."
    exit 0
} else {
    Write-Error "⚠️  SOME TESTS FAILED! Please check the detailed output above."
    exit 1
}
