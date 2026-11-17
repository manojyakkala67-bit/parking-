#!/usr/bin/env bash
# Smart Parking Management System - Unix/macOS Launcher
echo "========================================================"
echo " LAUNCHING SMART PARKING MANAGEMENT SYSTEM"
echo "========================================================"
java -cp "target/classes:lib/mysql-connector-j.jar" com.smartparking.Main
