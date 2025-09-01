#!/bin/bash

# WordCount Learning Example
# This script runs the WordCount MapReduce job for learning purposes

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

echo "📚 Running WordCount Learning Example..."

# Check if input file exists
INPUT_FILE="$PROJECT_DIR/input/shakespeare.txt"
if [ ! -f "$INPUT_FILE" ]; then
    echo "❌ Input file not found: $INPUT_FILE"
    echo "   Please ensure the Shakespeare text file is available."
    exit 1
fi

# Create output directory
OUTPUT_DIR="$PROJECT_DIR/output/wordcount"
mkdir -p "$OUTPUT_DIR"

# Check if output directory is empty
if [ "$(ls -A "$OUTPUT_DIR" 2>/dev/null)" ]; then
    echo "⚠️  Output directory is not empty. Clearing..."
    rm -rf "$OUTPUT_DIR"/*
fi

echo "📖 Processing Shakespeare's complete works..."
echo "   Input: $INPUT_FILE"
echo "   Output: $OUTPUT_DIR"

# Run the WordCount example using the local test version
cd "$PROJECT_DIR"
mvn exec:java -pl wordcount -Dexec.mainClass="solution.WordCountLocalTest" -Dexec.args="$INPUT_FILE $OUTPUT_DIR/wordcount-results.txt" -q

echo ""
echo "✅ WordCount completed successfully!"
echo ""
echo "📊 Results:"
echo "   Output file: $OUTPUT_DIR/wordcount-results.txt"
echo "   File size: $(du -h "$OUTPUT_DIR/wordcount-results.txt" | cut -f1)"
echo "   Word count: $(wc -l < "$OUTPUT_DIR/wordcount-results.txt") unique words"
echo ""
echo "🔍 Sample results:"
head -10 "$OUTPUT_DIR/wordcount-results.txt"
echo ""
echo "🎓 Learning points:"
echo "   - This demonstrates the classic MapReduce word counting pattern"
echo "   - The mapper extracts words from text and emits (word, 1) pairs"
echo "   - The reducer sums up the counts for each word"
echo "   - This is the foundation for many text processing applications"
