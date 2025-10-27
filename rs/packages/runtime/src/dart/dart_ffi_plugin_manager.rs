// Copyright (c) 2025. NESP Technology Corporation. All rights reserved.
// 
// This program is not free software; you can't redistribute it and/or modify it
// without the permit of team manager.
// 
// Unless required by applicable law or agreed to in writing.
// 
// If you have any questions or if you find a bug,
// please contact the author by email or ask for Issues.

#[flutter_rust_bridge::frb(sync)]
pub fn plugin_manager_init() {

}

#[flutter_rust_bridge::frb(init)]
pub fn init_app() {
    // Default utilities - feel free to customize
    flutter_rust_bridge::setup_default_user_utils();
}
