# GitHub Workflows

This directory contains GitHub Actions workflows for the Hadoop MapReduce Demo project.

## Workflows

### 1. CI/CD Pipeline (`ci.yml`)

**Triggers:**
- Push to main, develop, or 2025 branches
- Pull requests to main, develop, or 2025 branches
- Manual workflow dispatch

**Jobs:**
- **Build and Test**: Compiles and tests the project with Java 17 and 21
- **Code Quality**: Runs code quality checks and validation
- **WordCount Demo**: Executes the WordCount example to verify functionality
- **Docker Build**: Builds Docker images for containerized deployment
- **Security Scan**: Checks for security vulnerabilities
- **Documentation**: Validates documentation files
- **Notify**: Provides summary of all job results

**Features:**
- Multi-Java version testing (17, 21)
- Maven dependency caching
- Artifact uploads for test results and build outputs
- Comprehensive error reporting

### 2. Release Management (`release.yml`)

**Triggers:**
- Git tags (v*)
- Manual workflow dispatch with version input

**Features:**
- Automated release creation
- Artifact packaging
- GitHub release generation
- Release notes generation

**Usage:**
```bash
# Create a release tag
git tag v2.1.0
git push origin v2.1.0

# Or trigger manually via GitHub UI
```

### 3. Dependency Updates (`dependency-update.yml`)

**Triggers:**
- Weekly schedule (Mondays at 9 AM UTC)
- Manual workflow dispatch

**Features:**
- Dependency vulnerability scanning
- Outdated dependency detection
- Automated dependency updates (when enabled)

## Configuration

### Environment Variables

The workflows use the following environment variables:

- `MAVEN_OPTS`: Maven JVM options (`-Xmx1024m -XX:+UseG1GC`)
- `JAVA_OPTS`: Java JVM options (`-Xmx1024m`)

### Secrets

The following secrets are used (if available):

- `GITHUB_TOKEN`: Automatically provided by GitHub Actions
- Custom secrets can be added in repository settings

### Caching

The workflows implement caching for:
- Maven dependencies (`~/.m2`)
- Docker build cache
- Java setup cache

## Customization

### Adding New Jobs

To add a new job to the CI pipeline:

1. Add a new job definition in `ci.yml`
2. Update the `notify` job dependencies
3. Add appropriate triggers and conditions

### Modifying Test Matrix

To test with different Java versions:

```yaml
strategy:
  matrix:
    java-version: [11, 17, 21]
```

### Adding Security Scanning

To add comprehensive security scanning:

1. Add OWASP Dependency Check plugin to `pom.xml`
2. Update the security-scan job to use the plugin
3. Configure vulnerability reporting

### Docker Integration

The Docker build job can be extended to:
- Push to container registries
- Multi-architecture builds
- Automated deployment

## Troubleshooting

### Common Issues

1. **Maven Build Failures**
   - Check Java version compatibility
   - Verify Maven dependencies
   - Review build logs for specific errors

2. **Test Failures**
   - Ensure test data is available
   - Check test environment setup
   - Review test configuration

3. **Docker Build Issues**
   - Verify Dockerfile syntax
   - Check base image availability
   - Review build context

### Debug Mode

To enable debug logging, add this to workflow steps:

```yaml
- name: Debug step
  run: |
    echo "Debug information"
    env | sort
  env:
    ACTIONS_STEP_DEBUG: true
```

## Best Practices

1. **Keep workflows fast**: Use caching and parallel jobs
2. **Fail fast**: Use `continue-on-error` sparingly
3. **Clear naming**: Use descriptive job and step names
4. **Documentation**: Keep this README updated
5. **Security**: Regularly update actions and dependencies
6. **Monitoring**: Set up notifications for workflow failures

## Support

For issues with workflows:
1. Check the Actions tab in GitHub
2. Review workflow logs
3. Create an issue with workflow details
4. Check GitHub Actions documentation
