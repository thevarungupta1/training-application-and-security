# Lab 01: Windows Developer Workstation Readiness

**Audience:** Developers joining the mortgage-platform training  
**Platform:** Windows 11 (Windows 10 version 2004, build 19041 or later is also supported for WSL)  
**Shells:** PowerShell and Ubuntu on WSL 2  
**Estimated time:** 90-120 minutes, excluding software downloads and restarts  
**Last reviewed:** 27 August 2026

## Purpose

In this lab you will prepare and verify a Windows development workstation. You will:

- confirm the Windows architecture and virtualization prerequisites;
- install and configure WSL 2 with Ubuntu;
- create a Linux development workspace and use it from VS Code;
- verify Git, Java, Python, Docker Desktop, PostgreSQL, and IntelliJ IDEA;
- create a synthetic PostgreSQL database;
- configure an SSH key without exposing the private key; and
- troubleshoot PATH, port, package, and file-permission failures.

Do not install software or change system settings if your organization manages the workstation. Use the approved software catalog and request administrator assistance when required.

## Completion criteria

The lab is complete when you can show:

- `wsl --list --verbose` reports Ubuntu with version `2`;
- the Linux workspace opens in VS Code with a WSL indicator;
- `git`, `java`, `javac`, `python`, and `docker` return versions;
- `docker run --rm hello-world` completes successfully;
- PostgreSQL accepts a connection and returns the synthetic borrower row;
- `ssh-add -l` lists the training key; and
- the permission exercise ends with the file readable by its owner and group.

## Architecture and operating model

```text
Windows 11
|-- PowerShell / Windows Terminal
|-- Git for Windows
|-- JDK 25 (or the organization-approved JDK)
|-- Python (the organization-approved version)
|-- VS Code
|-- IntelliJ IDEA
|-- Docker Desktop
|-- PostgreSQL (Windows installation for this lab)
`-- WSL 2
    `-- Ubuntu
        |-- Bash, Git, SSH, and Linux utilities
        `-- ~/projects (Linux-side development workspace)
```

Keep projects on the filesystem used by the tools that build them. For Linux tools, use `~/projects`; for Windows-only tools, use a Windows path. Accessing `/mnt/c` is useful for interoperability, but cross-filesystem builds can be slower.

## 1. Verify Windows

Open **PowerShell** and run:

```powershell
winver
Get-ComputerInfo | Select-Object WindowsProductName, WindowsVersion, OsArchitecture
```

**Record:** Windows 10 version 2004/build 19041 or later, Windows 11, and `64-bit` architecture.

Check virtualization support:

```powershell
systeminfo
```

Review the **Hyper-V Requirements** section. WSL 2 requires hardware virtualization enabled in firmware and the Windows virtualization components. If the requirements are not met, stop and contact IT; do not change firmware settings without approval.

## 2. Install and verify WSL 2

Open **PowerShell as Administrator** and run:

```powershell
wsl --install
```

This enables the required components, installs the WSL kernel, sets WSL 2 as the default, and installs Ubuntu by default. Restart Windows when prompted.

After the restart, launch **Ubuntu** from the Start menu and create a Linux username and password. These credentials are separate from your Windows credentials. Password input is intentionally invisible.

In PowerShell, update WSL and inspect the distribution:

```powershell
wsl --update
wsl --status
wsl --list --verbose
```

The expected distribution row is similar to:

```text
NAME      STATE      VERSION
Ubuntu    Stopped    2
```

`Running` is also valid. The state changes as the distribution starts and stops; the important value for this lab is `VERSION 2`.

If Ubuntu is missing, list available distributions and install it explicitly:

```powershell
wsl --list --online
wsl --install --distribution Ubuntu
```

If Ubuntu is version 1, convert it:

```powershell
wsl --set-version Ubuntu 2
```

## 3. Configure Ubuntu

Open Ubuntu or enter it from PowerShell:

```powershell
wsl
```

Run these commands in the **Ubuntu Bash shell**:

```bash
whoami
pwd
sudo apt update && sudo apt upgrade -y
sudo apt install -y build-essential curl git jq tree unzip zip openssh-client
```

Verify the Linux tools:

```bash
git --version
curl --version
tree --version
```

## 4. Explore Windows/Linux integration

In Ubuntu, Windows drives are mounted below `/mnt`:

```bash
cd /mnt/c
ls
cd /mnt/c/Users
ls
```

For example, `C:\Users\<WindowsUser>` is approximately `/mnt/c/Users/<WindowsUser>`. Replace `<WindowsUser>` with the actual Windows profile name; do not copy the example literally.

Return to your Linux home directory and create the training workspace:

```bash
cd ~
mkdir -p ~/projects/mortgage-platform
cd ~/projects/mortgage-platform
pwd
```

Expected pattern: `/home/<linux-user>/projects/mortgage-platform`.

## 5. Configure Git in both environments

Git configuration is separate in Windows and WSL. Configure each environment only with your real training identity.

In **PowerShell**:

```powershell
git --version
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
git config --global --get-regexp "^(user|core)\."
```

In **Ubuntu**:

```bash
git --version
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
git config --global --get-regexp '^(user|core)\.'
```

Use your organization's documented credential manager and line-ending policy when cloning repositories. Do not put access tokens or passwords in Git configuration files.

## 6. Install and verify Java

Java 25 is the current Java SE LTS release as of this lab date. Use JDK 25 unless the course or organization specifies another supported JDK. Use an approved OpenJDK distribution or other approved vendor; licensing and support requirements take precedence over this default.

Install the JDK using the approved Windows software source. Then open a **new PowerShell window** and run:

```powershell
java --version
javac --version
where.exe java
```

The output must show a JDK installation, not only a JRE. If the training requires `JAVA_HOME`, set it to the JDK home directory, for example:

```text
C:\Program Files\Java\jdk-25
```

Do not add quotes to the value. After IT or the installer updates PATH, open a new terminal and verify `java --version` and `javac --version` again.

## 7. Install and verify Python

Install the organization-approved Python version from the approved source. Python version selection is a course dependency and must not be guessed from this document.

In a new **PowerShell** window:

```powershell
python --version
py --version
where.exe python
python -c "print('Mortgage environment ready')"
```

Expected output from the last command:

```text
Mortgage environment ready
```

Create and test a project virtual environment:

```powershell
New-Item -ItemType Directory -Force C:\Projects\mortgage-platform | Out-Null
Set-Location C:\Projects\mortgage-platform
python -m venv .venv
.\.venv\Scripts\Activate.ps1
python -m pip install --upgrade pip
python -m pip install requests
python -m pip show requests
deactivate
```

If PowerShell blocks activation, do not weaken the machine-wide policy. Use the organization-approved policy or run the environment's Python directly with `\.venv\Scripts\python.exe`.

## 8. Install VS Code and open the WSL workspace

Install VS Code on **Windows**, then install the **WSL** extension. Add the **Python**, **Extension Pack for Java**, and **Docker** extensions only if approved for the training image.

From Ubuntu:

```bash
cd ~/projects/mortgage-platform
code .
```

Verify that the new VS Code window shows a WSL indicator and that a new integrated terminal opens in Ubuntu. Commands and extensions for the remote workspace run in WSL; UI extensions can run locally.

## 9. Configure IntelliJ IDEA

Install IntelliJ IDEA using the approved edition and source. A standalone JDK is required for Java development; the IDE's bundled runtime is not a project JDK.

In IntelliJ IDEA, open **File > Project Structure > Project** and select the approved JDK (JDK 25 unless the course specifies otherwise). Create a small Java project and run:

```java
public class EnvironmentTest {
    public static void main(String[] args) {
        System.out.println("Mortgage developer workstation ready");
    }
}
```

Expected output:

```text
Mortgage developer workstation ready
```

## 10. Install and verify Docker Desktop

Before installing Docker Desktop, remove any separately installed Docker Engine or Docker CLI inside Ubuntu if present. Two engines can conflict.

Install the current Docker Desktop for Windows using the approved source. In Docker Desktop, confirm **Settings > General > Use WSL 2 based engine** when that option is available. Under **Settings > Resources > WSL Integration**, enable the Ubuntu distribution used by this lab.

Run the following in **PowerShell** and then in **Ubuntu**:

```text
docker version
docker run --rm hello-world
```

The command must print the hello-world success message. Do not install a second Docker Engine inside Ubuntu when Docker Desktop WSL integration is enabled.

## 11. Install and verify PostgreSQL

Install PostgreSQL and the command-line tools using the approved installer. Install pgAdmin only if it is part of the training image. Choose a strong local training password and store it only in the approved password manager; never use a shared or production password.

In **PowerShell**:

```powershell
psql --version
Get-Service | Where-Object { $_.Name -like "*postgres*" }
```

If `psql` is not found, locate the approved PostgreSQL `bin` directory and use the organization's PATH procedure. Do not edit PATH until you understand which installation is active.

## 12. Create a synthetic mortgage database

Connect with the local training account. PowerShell will prompt for the password; do not place it in the command line or in this file.

```powershell
psql -U postgres -h localhost -d postgres
```

Run this SQL at the `postgres=#` prompt:

```sql
CREATE DATABASE mortgage_platform;
\c mortgage_platform
CREATE TABLE borrowers (
    borrower_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    borrower_reference varchar(50) NOT NULL,
    status varchar(30) NOT NULL
);
INSERT INTO borrowers (borrower_reference, status)
VALUES ('BORROWER-1001', 'ACTIVE');
SELECT * FROM borrowers;
```

The query must return the one synthetic row. Exit with `\q`. This lab must use synthetic data only; never enter real borrower PII.

## 13. Configure an SSH key safely

In Ubuntu, generate a key for the approved Git platform. Use a passphrase unless policy explicitly says otherwise:

```bash
mkdir -p ~/.ssh
chmod 700 ~/.ssh
ssh-keygen -t ed25519 -C "your.email@example.com" -f ~/.ssh/id_ed25519
chmod 600 ~/.ssh/id_ed25519
chmod 644 ~/.ssh/id_ed25519.pub
ls -la ~/.ssh
```

The private key is `~/.ssh/id_ed25519`; the public key is `~/.ssh/id_ed25519.pub`. Upload only the public key to the approved Git platform. Inspect it without exposing the private key:

```bash
cat ~/.ssh/id_ed25519.pub
eval "$(ssh-agent -s)"
ssh-add ~/.ssh/id_ed25519
ssh-add -l
```

Do not paste the private key into chat, tickets, repositories, or screenshots. Use the Git platform's documented host-key verification and connection-test command when you are ready to connect.

## 14. Troubleshooting exercises

### PATH diagnosis

In PowerShell, use:

```powershell
where.exe java
$env:JAVA_HOME
$env:Path -split ";"
```

The first matching executable on PATH is selected. After correcting an installation or PATH entry, close all affected terminals, open a new one, and repeat the version check.

For Python, compare:

```powershell
where.exe python
python --version
py --list
```

### Port diagnosis

If PostgreSQL cannot bind to port 5432:

```powershell
Get-NetTCPConnection -LocalPort 5432 -ErrorAction SilentlyContinue |
    Select-Object LocalAddress, LocalPort, State, OwningProcess
```

Then inspect the owning process:

```powershell
Get-Process -Id <PID>
```

Replace `<PID>` with the numeric process ID. Do not stop a process owned by another user or service without approval. The same method applies to ports 8080, 3000, and 5000.

### Linux package diagnosis

In Ubuntu:

```bash
command -v curl || true
curl --version
sudo apt update
sudo apt install -y curl
curl --version
```

The diagnostic sequence is: confirm the command exists, locate it, install or repair the package, then verify the version.

### File-permission exercise

Use synthetic data in the Linux workspace:

```bash
mkdir -p ~/mortgage-lab/incoming
printf 'loan_id,status\nLN-1001,CURRENT\n' > ~/mortgage-lab/incoming/loans.csv
chmod 000 ~/mortgage-lab/incoming/loans.csv
ls -l ~/mortgage-lab/incoming/loans.csv
cat ~/mortgage-lab/incoming/loans.csv
```

The `cat` command should fail for the normal file owner because all permissions were removed. Diagnose and restore owner/group read access:

```bash
stat ~/mortgage-lab/incoming/loans.csv
chmod 640 ~/mortgage-lab/incoming/loans.csv
ls -l ~/mortgage-lab/incoming/loans.csv
cat ~/mortgage-lab/incoming/loans.csv
```

The final command should print the two-line synthetic CSV.

## 15. Security checkpoint

Before finishing, confirm that you did not:

- use real borrower data;
- store a database password in this Markdown file, shell history, source code, or a `.env` file;
- commit a private SSH key;
- upload `id_ed25519` instead of `id_ed25519.pub`; or
- install an unmanaged duplicate of Docker Engine inside WSL.

## Instructor verification

Ask the learner to demonstrate the completion criteria, not just show installation screens. The instructor should capture versions and configuration as environment evidence, confirm Docker works from both shells, run the synthetic database query, and verify that no secrets or real PII were introduced.

## References checked

- [Install WSL](https://learn.microsoft.com/en-us/windows/wsl/install)
- [Set up a WSL development environment](https://learn.microsoft.com/en-us/windows/wsl/setup/environment)
- [Docker Desktop WSL 2 backend](https://docs.docker.com/desktop/features/wsl/)
- [Developing in WSL with VS Code](https://code.visualstudio.com/docs/remote/wsl)
- [Oracle Java downloads and release status](https://www.oracle.com/java/technologies/downloads/)
- [IntelliJ IDEA SDK configuration](https://www.jetbrains.com/help/idea/sdk.html)
- [PostgreSQL Windows installers](https://www.postgresql.org/download/windows/)

Vendor pages and software versions change. Re-check the approved organization catalog and the linked first-party pages before each classroom delivery.
