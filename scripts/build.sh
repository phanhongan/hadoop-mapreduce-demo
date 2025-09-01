#!/bin/bash

# Build script for Hadoop MapReduce Learning Project
# This script builds all modules for learning purposes

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

echo "🎓 Building Hadoop MapReduce Learning Project..."

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed or not in PATH"
    echo "   Please install Maven 3.9+ to continue"
    exit 1
fi

# Check Maven version
MAVEN_VERSION=$(mvn -version | head -1 | awk '{print $3}')
echo "📦 Using Maven version: $MAVEN_VERSION"

# Check if Java 17+ is available
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed or not in PATH"
    echo "   Please install Java 17+ to continue"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -1 | awk -F'"' '{print $2}')
echo "☕ Using Java version: $JAVA_VERSION"

# Build all modules
echo "🔨 Building all modules..."
cd "$PROJECT_DIR"
mvn clean compile -DskipTests

echo "✅ Build completed successfully!"
echo ""
echo "🎯 Next steps:"
echo "   1. Run WordCount example: ./scripts/run-wordcount.sh"
echo "   2. Explore other examples: mvn exec:java -pl <module> -Dexec.mainClass=\"solution.<MainClass>\" -Dexec.args=\"<args>\""
echo "   3. Run tests: mvn test -pl mrunit"
echo "   4. Check documentation: docs/getting-started.md"
