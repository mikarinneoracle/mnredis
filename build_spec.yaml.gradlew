version: 0.1             
component: build
timeoutInSeconds: 5000
shell: bash
env:
  exportedVariables:
    - buildId
steps:
  - type: Command
    command: |
      chmod +x ./gradlew
      ./gradlew dockerBuild
