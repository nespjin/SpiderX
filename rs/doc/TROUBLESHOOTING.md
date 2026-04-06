# Troubleshooting Guide

## Common Compilation Issues After Optimization

### Issue 1: Workspace Lints Not Found

**Error Message:**
```
error: failed to load manifest for workspace member `/path/to/packages/runtime`
caused by: error inheriting `lints` from workspace root manifest's `workspace.lints.clippy`
```

**Solution:**
Make sure all packages have `[lints] workspace = true` in their `Cargo.toml`:

```toml
[package]
name = "runtime"
version = "0.1.0"
edition = "2024"

[lints]
workspace = true
```

This has been added to all packages:
- ✅ packages/runtime/Cargo.toml
- ✅ packages/compiler/Cargo.toml
- ✅ packages/core/Cargo.toml
- ✅ packages/loader/Cargo.toml

### Issue 2: Method Name Mismatch

**Error Message:**
```
error[E0599]: no method named `throw_java_expception_if_error` found for struct `JniHandler`
```

**Solution:**
The method was renamed from `throw_java_expception_*` to `throw_java_exception_*` (fixed typo).

All occurrences have been updated in:
- jni_handler.rs (definition)
- jni_plugin_manager.rs (11 call sites)
- jni_screen_type.rs (2 call sites)

### Issue 3: Double Error Unwrapping

**Error Message:**
```
error[E0277]: the `?` operator can only be used on `Result` or `Option`
```

**Solution:**
Changed `Ok(result?)` to just `result` in dataset_repository.rs line 206.

When a function already returns `Result<T, E>`, and you have a variable of type `Result<T, E>`, just return it directly:

```rust
// Wrong
let result: Result<String, String> = some_operation();
Ok(result?)  // Double unwrapping

// Correct
let result: Result<String, String> = some_operation();
result  // Direct return
```

### Issue 4: Missing Semicolons or Braces

**Error Message:**
```
error: expected `;`, found `}`
```

**Solution:**
When using the `?` operator at the end of a function, don't add a semicolon:

```rust
// Wrong
self.repository.do_something()?;

// Correct (if this is the last expression)
self.repository.do_something()?
```

Or wrap in Ok():
```rust
// Also correct
Ok(self.repository.do_something()?)
```

### Issue 5: Borrow Checker Errors

**Error Message:**
```
error[E0502]: cannot borrow `*self` as mutable because it is also borrowed as immutable
```

**Solution:**
Use scoped blocks to limit borrow lifetime:

```rust
{
    let value = self.get_value();  // Immutable borrow ends here
}
self.set_value(value);  // Mutable borrow OK
```

## Quick Fixes

### If Build Fails

1. **Clean and rebuild:**
   ```bash
   cd rs
   cargo clean
   cargo build
   ```

2. **Update dependencies:**
   ```bash
   cargo update
   ```

3. **Check Rust version:**
   ```bash
   rustc --version  # Should be 1.85+ for edition 2024
   ```

4. **Run verification script:**
   ```bash
   ./verify_build.sh
   ```

### If Clippy Complains

1. **Auto-fix common issues:**
   ```bash
   cargo clippy --fix --allow-staged --allow-dirty
   ```

2. **Allow specific lint temporarily:**
   ```rust
   #[allow(clippy::too_many_arguments)]
   fn my_function(...) { }
   ```

3. **Disable workspace lints for one package:**
   Remove or comment out `[lints] workspace = true` in that package's Cargo.toml

## Verification Steps

After fixing any issues, run:

```bash
# 1. Format code
cargo fmt --all

# 2. Check compilation
cargo check --all-targets

# 3. Run clippy
cargo clippy --all-targets --all-features

# 4. Run tests
cargo test --lib

# 5. Or use the verification script
./verify_build.sh
```

## Getting Help

If you encounter an error not listed here:

1. Read the error message carefully - Rust errors are very descriptive
2. Check the line number mentioned in the error
3. Look at the suggested fix in the error message
4. Search for the error code (e.g., E0599) in the Rust documentation
5. Check recent changes in the file mentioned

## Rollback Changes

If optimizations cause issues, you can revert specific files:

```bash
# Revert a specific file
git checkout HEAD -- rs/packages/runtime/src/plugin_manager.rs

# Revert all Rust changes
git checkout HEAD -- rs/
```

## Contact

For persistent issues, check:
- Rust documentation: https://doc.rust-lang.org/
- Clippy lints: https://rust-lang.github.io/rust-clippy/
- Community forums: https://users.rust-lang.org/
