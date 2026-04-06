#!/bin/bash

# Build Verification Script for SpiderX Rust Codebase
# This script checks if the code compiles correctly after optimizations

set -e  # Exit on error

echo "========================================="
echo "SpiderX Rust Build Verification"
echo "========================================="
echo ""

# Change to rs directory
cd "$(dirname "$0")"

echo "Step 1: Checking code formatting..."
cargo fmt --all --check && echo "✓ Formatting OK" || echo "✗ Formatting issues found"
echo ""

echo "Step 2: Running Clippy linter..."
cargo clippy --all-targets --all-features -- -D warnings && echo "✓ Clippy OK" || echo "✗ Clippy warnings found"
echo ""

echo "Step 3: Checking compilation (debug)..."
cargo check --all-targets && echo "✓ Debug check OK" || echo "✗ Debug compilation failed"
echo ""

echo "Step 4: Checking compilation (release)..."
cargo check --all-targets --release && echo "✓ Release check OK" || echo "✗ Release compilation failed"
echo ""

echo "Step 5: Running tests..."
cargo test --lib && echo "✓ Tests passed" || echo "✗ Tests failed"
echo ""

echo "========================================="
echo "Build Verification Complete!"
echo "========================================="
echo ""
echo "If all steps passed with ✓, your code is ready!"
echo "If any step failed with ✗, please fix the issues above."
