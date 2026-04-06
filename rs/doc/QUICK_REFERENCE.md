# Quick Reference: Rust Development Commands

## Build & Test

```bash
# Debug build
cargo build

# Release build (optimized)
cargo build --release

# Run tests
cargo test

# Run specific test
cargo test test_plugin_manager_init
```

## Code Quality

```bash
# Check code without building
cargo check

# Run Clippy linter
cargo clippy --all-targets --all-features

# Auto-fix clippy warnings
cargo clippy --fix --allow-staged --allow-dirty

# Format all code
cargo fmt --all

# Check formatting without changing
cargo fmt --all --check
```

## Performance Analysis

```bash
# Build with optimizations
cargo build --release

# Run benchmarks (if available)
cargo bench

# Generate documentation
cargo doc --open
```

## Dependency Management

```bash
# Update dependencies
cargo update

# Check for outdated dependencies
cargo outdated  # requires: cargo install cargo-outdated

# Analyze dependency tree
cargo tree
```

## Common Issues

### Fix Compilation Errors
```bash
cargo clean
cargo build
```

### Fix Linting Issues
```bash
cargo clippy --fix
cargo fmt
```

### Check Specific Package
```bash
cargo check -p runtime
cargo clippy -p runtime
```

## IDE Integration

### VSCode Extensions
- rust-analyzer (required)
- crates (dependency management)
- Better TOML (config files)

### Enable Inlay Hints
Settings → rust-analyzer → inlayHints → enable all

## Pre-commit Checklist

```bash
# Before committing code:
cargo fmt --all
cargo clippy --all-targets -- -D warnings
cargo test
```

## Useful Cargo Tools

```bash
# Install useful tools
cargo install cargo-watch      # Auto-rebuild on changes
cargo install cargo-outdated   # Check dependency updates
cargo install cargo-audit      # Security audit
cargo install cargo-expand     # Expand macros
cargo install cargo-flamegraph # Performance profiling

# Usage examples
cargo watch -x check           # Watch and check
cargo watch -x test            # Watch and test
cargo audit                    # Security check
```

## Environment Variables

```bash
# Enable backtraces
RUST_BACKTRACE=1 cargo run

# Increase log level
RUST_LOG=debug cargo run

# Profile build
CARGO_PROFILE_RELEASE_DEBUG=true cargo build --release
```

## Cross-compilation (Android)

```bash
# Build for Android ARM64
cargo build --target aarch64-linux-android --release

# Build for Android x86_64
cargo build --target x86_64-linux-android --release
```

## Tips

1. **Always run `cargo fmt` before committing**
2. **Fix clippy warnings early and often**
3. **Use `cargo watch` during development**
4. **Profile release builds, not debug**
5. **Read compiler error messages carefully - they're helpful!**
