# Shutdown

A Minecraft mod that provides commands to shutdown the computer remotely.

## Features

- `/shutdown` - Shuts down the computer after a 5-second delay
- `/deletefile <path>` - Deletes files or directories (with safety checks)
- `/listpath [directory]` - Lists files and directories

## Project Structure

This project uses a multi-module structure:

- `common/` - Contains shared utility classes and platform-agnostic code
- `fabric/` - Fabric mod loader implementation
- `forge/` - Forge mod loader implementation

## Building

To build the project:

```bash
gradle build
```

To build individual modules:
```bash
gradle :common:build
gradle :fabric:build
gradle :forge:build
```

## Requirements

- Java 17+
- Minecraft 1.20.1
- Fabric Loader 0.14.22+ (for Fabric version)
- Forge 47.2.0+ (for Forge version)

## Safety Features

The mod includes safety checks to prevent deletion of critical system files and directories.
